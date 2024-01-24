package org.example.Request;

public class RequestLine {
    private final String method;
    private final String path;
    private final String versionHTTP;

    public RequestLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.versionHTTP = version;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + versionHTTP + '\'' +
                '}';
    }
}
