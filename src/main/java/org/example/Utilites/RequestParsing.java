package org.example.Utilites;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.example.Request.Request;
import org.example.Request.RequestLine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestParsing {

    static List<String> validPaths = Stream.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js").collect(Collectors.toList());

    public static Optional<Request> parseRequest(BufferedInputStream in, BufferedOutputStream out) throws IOException, URISyntaxException {
        final int limit = 4096;

        in.mark(limit);
        final byte[] buffer = new byte[limit];
        final int read = in.read(buffer);
        Optional<List<NameValuePair>> queryParams = Optional.empty();
        RequestLine requestLine;
        Request request = null;

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
        if (parts[1].contains("?")) {
            queryParams = Optional.ofNullable(URLEncodedUtils.parse(new URI(parts[1]), StandardCharsets.UTF_8));
            parts[1] = parts[1].split("\\?")[0];
        }

        if (!validPaths.contains(parts[1])) {
            Response.badRequest(out);
            return Optional.empty();
        }

        requestLine = new RequestLine(parts[0], parts[1], parts[2]);

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
        if (queryParams.isPresent()) {
            request = new Request.RequestBuilder(requestLine, headers).setQueryParams(queryParams.get()).build();
        } else {
            request = new Request.RequestBuilder(requestLine, headers).build();
        }

        if (!requestLine.getMethod().equals("GET")) {
            in.skip(headersDelimiter.length);
            final Optional<String> contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final int length = Integer.parseInt(contentLength.get());
                final byte[] bodyBytes = in.readNBytes(length);
                final String body = new String(bodyBytes);
                if (queryParams.isPresent()) {
                    request = new Request.RequestBuilder(requestLine, headers).setBody(body).setQueryParams(queryParams.get()).build();
                } else {
                    request = new Request.RequestBuilder(requestLine, headers).setBody(body).build();
                }
            }
        }

        return Optional.ofNullable(request);
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

    public static void addValidPath(String path) {
        validPaths.add(path);
    }

}
