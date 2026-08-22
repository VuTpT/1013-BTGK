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
    args = ap.parse_args()

    if not os.path.isdir(args.results):
        print("LOI: khong thay thu muc ket qua %s" % args.results)
        return 1

    stats, broken = read_results(args.results)
    total, line = summary_line(stats)
    if total == 0:
        print("LOI: %s khong co file *-result.json nao" % args.results)
        return 1

    verdict = "DAT" if not broken else "HONG (%d test)" % len(broken)
    print("Ket qua: %d test -> %s  [%s]" % (total, line, verdict))

    base = os.environ.get("JIRA_BASE_URL", "")
    email = os.environ.get("JIRA_EMAIL", "")
    token = os.environ.get("JIRA_API_TOKEN", "")
    if not args.dry_run and not all([base, email, token]):
        print("LOI: thieu JIRA_BASE_URL / JIRA_EMAIL / JIRA_API_TOKEN")
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
            print("  LOI HTTP %s: %s" % (e.code, e.read().decode("utf-8", "replace")[:200]))
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

    print("Da gui %d yeu cau toi Jira%s." % (sent, " (dry-run)" if args.dry_run else ""))
    return 0


if __name__ == "__main__":
    sys.exit(main())
