package io.study.dubbo.starter;

import java.util.concurrent.CountDownLatch;

import io.study.dubbo.starter.client.Client;
import io.study.dubbo.starter.server.Server;

public class App {

    private static String registryProtocol = "zookeeper://192.168.56.11:2181";

    public static void main(String[] args) throws Exception {
        startServer();
        startClient();

        new CountDownLatch(1).await();
    }

    public static void startServer() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Server server = new Server(registryProtocol);
                server.start();
            } catch (Exception e) {
                System.err.println("Server stopped!");
            }
        });
        thread.setDaemon(true);
        thread.setName("dubbo-starter-server");
        thread.start();
        System.out.println("Server started.");
    }

    public static void startClient() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Client client = new Client(registryProtocol);
                while(true) {
                    client.invoke();
                    Thread.sleep(1 * 1000);
                }
            } catch (Exception e) {
                System.err.println("Client stopped!");
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.setName("dubbo-starter-client");
        thread.start();
        System.out.println("Client started.");
    }

}
