package org.example.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
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
            while (true) {
                try {
                    pool.execute(new Runner(serverSocket.accept(), handlers));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            List<String> list = new ArrayList<>();
            for (Map.Entry entry : handlers.entrySet()) {
                list.add((String) entry.getKey());
            }
            if (!list.contains(path)) {
                Map<String, Handler> map = new ConcurrentHashMap<>();
                map.put(path, handler);
                handlers.put(method, map);
            }
        }
    }
}
