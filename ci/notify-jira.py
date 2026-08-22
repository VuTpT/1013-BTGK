#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Doc ket qua Allure roi day nguoc len Jira Cloud.

Lam 3 viec:
  1. Binh luan tong ket (pass/fail + link report) vao mot issue "dau moi".
  2. Voi moi test FAIL, binh luan vao dung cac issue KTPM ma test do gan bang @TmsLink.
  3. (tuy chon --create-bug) Tao issue Bug khi co test fail.

Chi dung thu vien chuan cua Python - khong can pip install.
Dung REST API v2 (khong phai v3): v2 nhan body la chuoi thuan, v3 bat buoc
phai dung Atlassian Document Format nen dai dong hon nhieu.

Bien moi truong bat buoc:
    JIRA_BASE_URL    vd: https://sinhvien-team-xej6z3t8.atlassian.net
    JIRA_EMAIL       email tai khoan Atlassian
    JIRA_API_TOKEN   tao tai id.atlassian.com/manage-profile/security/api-tokens

Vi du:
    python ci/notify-jira.py --results target/allure-results \
        --report-url https://vutpt.github.io/1013-BTGK/ \
        --launch-issue KTPM-1 --build "CI #42" --dry-run
"""
import argparse
import base64
import glob
import json
import os
import sys
import urllib.error
import urllib.request

TIMEOUT = 30

# Console Windows mac dinh dung cp1252, in ten tieng Viet co dau se vo.
for _stream in (sys.stdout, sys.stderr):
    if hasattr(_stream, "reconfigure"):
        _stream.reconfigure(encoding="utf-8", errors="replace")


# ------------------------------------------------------------- bao loi len CI
IN_ACTIONS = os.environ.get("GITHUB_ACTIONS") == "true"


def annotate(level, message):
    """In ra man hinh; neu dang chay tren GitHub Actions thi in them dang chu thich.

    Buoc goi script nay dat continue-on-error: true (su co Jira khong nen lam
    do ca build). Doi lai, loi phai hien that ro tren trang run - neu khong
    no se hong am tham va khong ai biet.
    """
    print(message)
    if IN_ACTIONS:
        one_line = message.replace("\n", " ")
        print("::%s::%s" % (level, one_line))
        summary = os.environ.get("GITHUB_STEP_SUMMARY")
        if summary and level in ("error", "warning"):
            try:
                with open(summary, "a", encoding="utf-8") as fh:
                    fh.write("**Jira: %s** - %s\n\n" % (level.upper(), one_line))
            except OSError:
                pass


# --------------------------------------------------------------------- doc .env
def load_env_file(path):
    """Nap bien tu file .env cho lan chay tai may.

    Bien da co san trong moi truong duoc GIU NGUYEN, khong bi file ghi de -
    nho vay tren GitHub Actions thi Secrets luon thang, con o may ca nhan thi
    file .env co tac dung. File .env khong bao gio duoc commit (xem .gitignore).
    """
    if not path or not os.path.isfile(path):
        return 0
    loaded = 0
    with open(path, encoding="utf-8") as fh:
        for raw in fh:
            line = raw.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            if line.startswith("export "):
                line = line[len("export "):]
            key, value = line.split("=", 1)
            key = key.strip()
            value = value.strip()
            if len(value) >= 2 and value[0] == value[-1] and value[0] in "\"'":
                value = value[1:-1]
            if key and key not in os.environ:      # moi truong that luon uu tien
                os.environ[key] = value
                loaded += 1
    return loaded


# ----------------------------------------------------------------- doc ket qua
def read_results(results_dir):
    """Tra ve (thong ke theo trang thai, danh sach test hong)."""
    stats = {}
    broken = []
    for path in glob.glob(os.path.join(results_dir, "*-result.json")):
        with open(path, encoding="utf-8") as fh:
            data = json.load(fh)
        status = data.get("status", "unknown")
        stats[status] = stats.get(status, 0) + 1
        if status in ("failed", "broken"):
            broken.append({
                "name": data.get("name") or data.get("fullName", "?"),
                "full": data.get("fullName", ""),
                "status": status,
                "message": (data.get("statusDetails") or {}).get("message", "").strip(),
                # chi lay link loai tms -> chinh la ma KTPM gan bang @TmsLink
                "issues": [l["name"] for l in data.get("links", [])
                           if l.get("type") == "tms" and l.get("name")],
            })
    broken.sort(key=lambda t: t["name"])
    return stats, broken


def summary_line(stats):
    total = sum(stats.values())
    order = ["passed", "failed", "broken", "skipped", "unknown"]
    parts = ["%s %d" % (k, stats[k]) for k in order if stats.get(k)]
    return total, " | ".join(parts) if parts else "khong co ket qua"


# --------------------------------------------------------------------- goi Jira
class Jira:
    def __init__(self, base_url, email, token, dry_run=False):
        self.base = base_url.rstrip("/")
        self.dry_run = dry_run
        raw = ("%s:%s" % (email, token)).encode("utf-8")
        self.auth = "Basic " + base64.b64encode(raw).decode("ascii")

    def _get(self, path):
        req = urllib.request.Request(
            "%s%s" % (self.base, path),
            headers={"Authorization": self.auth, "Accept": "application/json"},
            method="GET",
        )
        with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
            return json.loads(resp.read().decode("utf-8"))

    def whoami(self):
        return self._get("/rest/api/2/myself")

    def issue(self, key):
        return self._get("/rest/api/2/issue/%s?fields=summary,issuetype,status" % key)

    def _post(self, path, payload):
        url = "%s%s" % (self.base, path)
        if self.dry_run:
            print("  [DRY-RUN] POST %s" % url)
            print("            %s" % json.dumps(payload, ensure_ascii=False)[:300])
            return {"dry_run": True}
        req = urllib.request.Request(
            url,
            data=json.dumps(payload).encode("utf-8"),
            headers={"Authorization": self.auth,
                     "Content-Type": "application/json",
                     "Accept": "application/json"},
            method="POST",
        )
        with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
            body = resp.read().decode("utf-8")
        return json.loads(body) if body else {}

    def comment(self, issue_key, text):
        return self._post("/rest/api/2/issue/%s/comment" % issue_key, {"body": text})

    def create_bug(self, project_key, summary, description, issue_type="Bug"):
        return self._post("/rest/api/2/issue", {
            "fields": {
                "project": {"key": project_key},
                "summary": summary[:250],
                "description": description,
                "issuetype": {"name": issue_type},
            }
        })


# ------------------------------------------------------------------ kiem tra truoc
def preflight(args):
    """Doi chieu cau hinh voi Jira that: xac thuc dung chua, ma issue co ton tai khong."""
    base = os.environ.get("JIRA_BASE_URL", "")
    email = os.environ.get("JIRA_EMAIL", "")
    token = os.environ.get("JIRA_API_TOKEN", "")

    print("1) Bien moi truong")
    missing = [n for n, v in [("JIRA_BASE_URL", base), ("JIRA_EMAIL", email),
                              ("JIRA_API_TOKEN", token)] if not v]
    if missing:
        print("   THIEU: %s" % ", ".join(missing))
        return 1
    if base.endswith("/"):
        print("   CANH BAO: JIRA_BASE_URL co dau '/' o cuoi, nen bo di")
        base = base.rstrip("/")
    print("   JIRA_BASE_URL = %s" % base)
    print("   JIRA_EMAIL    = %s" % email)
    print("   JIRA_API_TOKEN= %s (%d ky tu)" % ("*" * 8, len(token)))

    jira = Jira(base, email, token)

    print()
    print("2) Xac thuc")
    try:
        me = jira.whoami()
        print("   OK - dang nhap voi tu cach: %s <%s>"
              % (me.get("displayName", "?"), me.get("emailAddress", "?")))
    except urllib.error.HTTPError as e:
        hint = {401: "sai email hoac API token",
                403: "token dung nhung tai khoan bi chan",
                404: "JIRA_BASE_URL sai (khong phai site Jira)"}.get(e.code, "")
        print("   LOI HTTP %s%s" % (e.code, " - " + hint if hint else ""))
        return 1
    except urllib.error.URLError as e:
        print("   LOI ket noi: %s" % e.reason)
        return 1

    # Gom ma KTPM tu ket qua Allure - chinh la cac ma @TmsLink trong code
    keys = set()
    if os.path.isdir(args.results):
        for path in glob.glob(os.path.join(args.results, "*-result.json")):
            with open(path, encoding="utf-8") as fh:
                data = json.load(fh)
            keys.update(l["name"] for l in data.get("links", [])
                        if l.get("type") == "tms" and l.get("name"))
    if args.launch_issue:
        keys.add(args.launch_issue)

    print()
    print("3) Doi chieu %d ma issue" % len(keys))
    if not keys:
        print("   Khong tim thay ma nao. Chay 'mvn clean test' truoc de sinh ket qua Allure.")
        return 1

    ok, bad = [], []
    for key in sorted(keys):
        tag = " (issue dau moi)" if key == args.launch_issue else ""
        try:
            data = jira.issue(key)
            f = data.get("fields", {})
            print("   OK    %-9s %-6s %s%s" % (
                key,
                (f.get("issuetype") or {}).get("name", "?"),
                (f.get("summary") or "")[:52], tag))
            ok.append(key)
        except urllib.error.HTTPError as e:
            print("   KHONG %-9s HTTP %s%s" % (key, e.code, tag))
            bad.append(key)

    print()
    print("Ket luan: %d ma dung, %d ma khong tim thay." % (len(ok), len(bad)))
    if bad:
        print("Cac ma khong ton tai: %s" % ", ".join(bad))
        print("-> Mo board Jira xem ma that, roi bao lai de sua @TmsLink trong code.")
        return 1
    print("-> Cau hinh dung, co the push len GitHub.")
    return 0


# -------------------------------------------------------------------- chuong trinh
def main():
    ap = argparse.ArgumentParser(description="Day ket qua Allure len Jira Cloud")
    ap.add_argument("--results", default="target/allure-results")
    ap.add_argument("--report-url", default="", help="Dia chi Allure report da publish")
    ap.add_argument("--launch-issue", default="", help="Issue nhan binh luan tong ket")
    ap.add_argument("--build", default="local", help="Nhan cua lan chay, vd 'CI #42'")
    ap.add_argument("--build-url", default="", help="Dia chi trang build tren CI")
    ap.add_argument("--create-bug", metavar="PROJECT_KEY", default="",
                    help="Tao issue Bug khi co test fail, vd --create-bug KTPM")
    ap.add_argument("--max-issue-comments", type=int, default=20,
                    help="Chan tren so binh luan gui vao cac issue le")
    ap.add_argument("--dry-run", action="store_true", help="In ra man hinh, khong goi Jira")
    ap.add_argument("--check", action="store_true",
                    help="Chi kiem tra: xac thuc + doi chieu ma issue co that khong. Khong ghi gi len Jira.")
    ap.add_argument("--env-file", default=".env",
                    help="File .env nap cho lan chay tai may (mac dinh: .env). Dat rong de bo qua.")
    args = ap.parse_args()

    n = load_env_file(args.env_file)
    if n:
        print("Da nap %d bien tu %s" % (n, args.env_file))

    if args.check:
        return preflight(args)

    if not os.path.isdir(args.results):
        annotate("error", "Khong thay thu muc ket qua Allure: %s" % args.results)
        return 1

    stats, broken = read_results(args.results)
    total, line = summary_line(stats)
    if total == 0:
        annotate("error", "%s khong co file *-result.json nao - test chua chay?" % args.results)
        return 1

    verdict = "DAT" if not broken else "HONG (%d test)" % len(broken)
    print("Ket qua: %d test -> %s  [%s]" % (total, line, verdict))

    base = os.environ.get("JIRA_BASE_URL", "")
    email = os.environ.get("JIRA_EMAIL", "")
    token = os.environ.get("JIRA_API_TOKEN", "")
    if not args.dry_run and not all([base, email, token]):
        thieu = ", ".join(n for n, v in [("JIRA_BASE_URL", base), ("JIRA_EMAIL", email),
                                         ("JIRA_API_TOKEN", token)] if not v)
        annotate("error", "Thieu bien: %s. File .env KHONG duoc commit nen CI khong thay -> "
                          "phai khai o Settings > Secrets and variables > Actions > Secrets." % thieu)
        return 1

    jira = Jira(base or "https://example.atlassian.net", email, token, args.dry_run)
    sent = 0

    # 1) binh luan tong ket
    if args.launch_issue:
        body = ["[%s] Ket qua kiem thu tu dong: %s" % (args.build, verdict),
                "Thong ke: %s (tong %d)" % (line, total)]
        if args.report_url:
            body.append("Bao cao Allure: %s" % args.report_url)
        if args.build_url:
            body.append("Nhat ky build: %s" % args.build_url)
        if broken:
            body.append("")
            body.append("Test hong:")
            body += ["  - %s%s" % (t["name"],
                                   (" [" + ", ".join(t["issues"]) + "]") if t["issues"] else "")
                     for t in broken[:15]]
            if len(broken) > 15:
                body.append("  ... con %d test nua, xem trong bao cao." % (len(broken) - 15))
        print("Binh luan tong ket -> %s" % args.launch_issue)
        try:
            jira.comment(args.launch_issue, "\n".join(body))
            sent += 1
        except urllib.error.HTTPError as e:
            hint = {401: " (sai email hoac API token)", 404: " (issue %s khong ton tai)" % args.launch_issue}.get(e.code, "")
            annotate("error", "Khong binh luan duoc vao %s: HTTP %s%s" % (args.launch_issue, e.code, hint))
            return 1

    # 2) binh luan vao dung issue cua tung test hong
    per_issue = {}
    for t in broken:
        for key in t["issues"]:
            per_issue.setdefault(key, []).append(t)

    if per_issue:
        print("Binh luan vao %d issue lien quan toi test hong" % len(per_issue))
    for key in sorted(per_issue)[:args.max_issue_comments]:
        tests = per_issue[key]
        body = ["[%s] Test gan voi issue nay dang HONG." % args.build]
        for t in tests:
            body.append("  - %s (%s)" % (t["name"], t["status"]))
            if t["message"]:
                body.append("    %s" % t["message"].splitlines()[0][:200])
        if args.report_url:
            body.append("Chi tiet: %s" % args.report_url)
        try:
            jira.comment(key, "\n".join(body))
            sent += 1
            print("  %s <- %d test" % (key, len(tests)))
        except urllib.error.HTTPError as e:
            print("  %s LOI HTTP %s" % (key, e.code))
    if len(per_issue) > args.max_issue_comments:
        print("  (bo qua %d issue do vuot --max-issue-comments)"
              % (len(per_issue) - args.max_issue_comments))

    # 3) tao Bug neu duoc yeu cau
    if args.create_bug and broken:
        summary = "[%s] %d test tu dong bi hong" % (args.build, len(broken))
        desc = ["Phat hien boi day chuyen kiem thu tu dong.",
                "Thong ke: %s (tong %d)" % (line, total)]
        if args.report_url:
            desc.append("Bao cao Allure: %s" % args.report_url)
        if args.build_url:
            desc.append("Nhat ky build: %s" % args.build_url)
        desc.append("")
        desc += ["- %s%s" % (t["name"], (" [" + ", ".join(t["issues"]) + "]") if t["issues"] else "")
                 for t in broken]
        try:
            created = jira.create_bug(args.create_bug, summary, "\n".join(desc))
            print("Da tao Bug: %s" % created.get("key", "(dry-run)"))
            sent += 1
        except urllib.error.HTTPError as e:
            print("  LOI tao Bug HTTP %s: %s" % (e.code, e.read().decode("utf-8", "replace")[:200]))

    msg = "Da gui %d yeu cau toi Jira%s." % (sent, " (dry-run)" if args.dry_run else "")
    if sent == 0 and not args.dry_run:
        annotate("warning", msg + " Khong co gi duoc gui - kiem tra --launch-issue.")
    else:
        annotate("notice", msg)
    return 0


if __name__ == "__main__":
    sys.exit(main())
