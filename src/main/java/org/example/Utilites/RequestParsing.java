package org.example.Utilites;

import org.example.Request.Request;
import org.example.Request.RequestLine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestParsing {

    static List<String > validPaths = Stream.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js").collect(Collectors.toList());

    public static Optional<Request> parseRequest(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        final int limit = 4096;

        in.mark(limit);
        final byte[] buffer = new byte[limit];
        final int read = in.read(buffer);

        final byte[] requestLineDelimiter = new byte[]{'\r', '\n'};
        final int requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            Response.badRequest(out);
            return Optional.empty();
        }

        final String[] parts = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (parts.length != 3) {
            Response.badRequest(out);
            return Optional.empty();
        }

        if (!validPaths.contains(parts[1])) {
            Response.badRequest(out);
            return Optional.empty();
        }

        RequestLine requestLine = new RequestLine(parts[0], parts[1], parts[2]);

        final byte[] headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final int headersStart = requestLineEnd + requestLineDelimiter.length;
        final int headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            Response.badRequest(out);
            return Optional.empty();
        }

        in.reset();

        in.skip(headersStart);

        final byte[] headersBytes = in.readNBytes(headersEnd - headersStart);
        final List<String> headers = Arrays.asList(new String(headersBytes).split("\r\n"));

        Request request = new Request(requestLine, headers);

        if (!requestLine.getMethod().equals("GET")) {
            in.skip(headersDelimiter.length);
            final Optional<String> contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final int length = Integer.parseInt(contentLength.get());
                final byte[] bodyBytes = in.readNBytes(length);

                final String body = new String(bodyBytes);
                request = new Request(requestLine, headers, body);
            }
        }

        return Optional.of(request);
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
    public static void addValidPath (String path) {
       validPaths.add(path);
    }

}
