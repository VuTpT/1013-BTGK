package vn.edu.ktpm.minishop.support;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web server tinh sieu nho (dung {@code com.sun.net.httpserver} co san trong JDK) de phuc vu
 * thu muc {@code sut-web}.
 *
 * <p>Nho lop nay, toan bo suite Selenium chay <b>offline</b>, khong phu thuoc Internet hay
 * mot web server cai ngoai - test case vi the tai lap 100%.</p>
 */
public class LocalWebServer {

    private final Path root;
    private HttpServer server;

    public LocalWebServer(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    /** Tim thu muc sut-web tu bat ky thu muc lam viec nao (module hoac thu muc goc). */
    public static Path defaultRoot() {
        String override = System.getProperty("sut.web.dir");
        if (override != null && !override.isBlank()) {
            return Paths.get(override);
        }
        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 5 && current != null; i++) {
            Path candidate = current.resolve("sut-web");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Khong tim thay thu muc sut-web, hay truyen -Dsut.web.dir=...");
    }

    public void start() throws IOException {
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException("Thu muc SUT khong ton tai: " + root);
        }
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String uri = exchange.getRequestURI().getPath();
            if ("/".equals(uri)) {
                uri = "/index.html";
            }
            Path file = root.resolve(uri.substring(1)).normalize();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] body = Files.readAllBytes(file);
            exchange.getResponseHeaders().add("Content-Type", contentType(file));
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/index.html";
    }

    private static String contentType(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (name.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (name.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        return "application/octet-stream";
    }
}
