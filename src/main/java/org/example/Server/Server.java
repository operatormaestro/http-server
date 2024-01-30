package org.example.Server;

import org.example.Utilites.RequestParsing;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService pool = Executors.newFixedThreadPool(64);
    private final int port;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void listenPort() {
        try (final var serverSocket = new ServerSocket(port)) {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    pool.execute(new Runner(serverSocket.accept(), handlers));
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            putMap(method, path, handler);
        } else {
            Map<String, Handler> map = handlers.get(method);
            if (!map.containsKey(path)) {
                putMap(method, path, handler);
            }
        }
    }

    public void putMap(String method, String path, Handler handler) {
        Map<String, Handler> map = new ConcurrentHashMap<>();
        map.put(path, handler);
        handlers.put(method, map);
        RequestParsing.addValidPath(path);
    }
}