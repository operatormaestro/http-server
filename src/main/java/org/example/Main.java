package org.example;

import org.example.Server.Handler;
import org.example.Server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(9999);

        Handler handler = (request, out) -> {
            try {
                final var path = request.getRequestLine().getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().plusHours(1).toString()).getBytes();
                final var requestTimer =
                        ("HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n").getBytes();

                out.write(requestTimer);
                out.write(content);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
//commentires
        server.addHandler("GET", "/messages/timer.html", handler); //для проверки работоспособности)
        server.addHandler("POST", "/messages", handler);
        server.listenPort();
    }
}

