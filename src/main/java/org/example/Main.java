package org.example;

import org.example.Request.Request;
import org.example.Server.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class    Main {
    public static void main(String[] args) {
        Server server = new Server(9999);
        server.listenPort();
    }
}

