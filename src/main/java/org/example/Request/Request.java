package org.example.Request;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final RequestLine requestLine;
    private final List<String> headers;
    private final String body;
    private List<NameValuePair> queryParams;


    private Request(RequestBuilder requestBuilder) {
        requestLine = requestBuilder.requestLine;
        headers = requestBuilder.headers;
        body = requestBuilder.body;
        queryParams = requestBuilder.queryParams;

    }
    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<String> getQueryParam(String param) {
        return queryParams.stream()
                .filter(o -> o.getName().startsWith(param))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
    }

    public static class RequestBuilder {
        private final RequestLine requestLine;
        private final List<String> headers;
        private String body;
        private List<NameValuePair> queryParams;

        public RequestBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public RequestBuilder setQueryParams(List<NameValuePair> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public RequestBuilder(RequestLine requestLine, List<String> headers) {
            this.requestLine = requestLine;
            this.headers = headers;
        }

        public Request build() {
            return new Request(this);
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}