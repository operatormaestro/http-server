package org.example.Server;

import org.example.Request.Request;
import org.example.Utilites.RequestParsing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.Utilites.Response.sendClassicResponse;
import static org.example.Utilites.Response.sendOkResponse;

public class Runner implements Runnable {
    private final Socket socket;
    private final Map<String, Map<String, Handler>> handlers;

    public Runner(Socket socket, Map<String, Map<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (
                final var in = new BufferedInputStream(socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream())) {
            Optional<Request> optionalRequest = RequestParsing.parseRequest(in, out);
            if (optionalRequest.isPresent()) {
                Request request = optionalRequest.get();
                final var path = request.getRequestLine().getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                String method = request.getRequestLine().getMethod();
                if (handlers.containsKey(method)) {
                    List<String> list = new ArrayList<>();
                    for (Map.Entry entry : handlers.entrySet()) {
                        list.add((String) entry.getKey());
                    }
                    if (!list.contains(path)) {
                        handlers.get(method).get(path).handle(request, out);
                    }
                }
                if (path.contains("/classic.html")) {
                    sendClassicResponse(filePath, mimeType, out);
                } else {
                    sendOkResponse(filePath, mimeType, out);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
