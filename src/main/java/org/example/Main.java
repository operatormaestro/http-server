package org.example;

import org.example.Request.Request;
import org.example.Request.RequestLine;
import org.example.Server.Server;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(9999);
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // TODO: handlers code
        });
        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // TODO: handlers code
        });
        server.listenPort();
    }
}

