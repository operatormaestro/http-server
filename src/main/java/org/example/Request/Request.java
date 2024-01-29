package org.example.Request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final RequestLine requestLine;
    private final List<String> headers;
    private String body;
    private List<NameValuePair> queryParams;

    public Request(RequestLine requestLine, List<String> headers, String body) throws URISyntaxException {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.queryParams = URLEncodedUtils.parse(new URI(requestLine.getPath()), StandardCharsets.UTF_8);
    }

    public Request(RequestLine requestLine, List<String> headers) throws URISyntaxException {
        this.requestLine = requestLine;
        this.headers = headers;
        this.queryParams = URLEncodedUtils.parse(new URI(requestLine.getPath()), StandardCharsets.UTF_8);

    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }
    public List<String> getQueryParam (String param) {
        return queryParams.stream()
                .filter(o -> o.getName().startsWith(param))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
    }
}