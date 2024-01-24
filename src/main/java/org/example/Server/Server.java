package org.example.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService pool = Executors.newFixedThreadPool(64);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void listenPort() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    pool.execute(new Runner(serverSocket.accept()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
