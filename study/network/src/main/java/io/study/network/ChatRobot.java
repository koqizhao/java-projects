package io.study.network;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mydotey.objectpool.facade.ThreadPools;
import org.mydotey.objectpool.threadpool.autoscale.AutoScaleThreadPool;
import org.mydotey.objectpool.threadpool.autoscale.AutoScaleThreadPoolConfig;

/**
 * @author koqizhao
 *
 * Jun 22, 2018
 */
public class ChatRobot implements Closeable {

    public static void main(String[] args) throws IOException {
        try (ChatRobot chatRobot = new ChatRobot(8080);) {
            chatRobot.start();
        }
    }

    private ServerSocket _serverSocket;
    private AutoScaleThreadPool _chatterThreadPool;

    private AtomicBoolean _isRunning;

    public ChatRobot(int port) throws IOException {
        _serverSocket = new ServerSocket(port);
        _serverSocket.setSoTimeout(10 * 1000);

        AutoScaleThreadPoolConfig config = ThreadPools.newAutoScaleThreadPoolConfigBuilder()
                .setMaxIdleTime(TimeUnit.SECONDS.toMillis(10)).setMinSize(5).setMaxSize(100).setQueueCapacity(100)
                .setScaleFactor(5).build();
        _chatterThreadPool = ThreadPools.newAutoScaleThreadPool(config);

        _isRunning = new AtomicBoolean();
    }

    public void start() {
        if (!_isRunning.compareAndSet(false, true)) {
            System.out.println("ChatRobot has been started!");
            return;
        }

        System.out.println("ChatRobot is serving!");
        while (_isRunning.get()) {
            try {
                Socket socket = _serverSocket.accept();
                _chatterThreadPool.submit(() -> chat(socket));
                System.out.println("Got a visitor, started a chatter");
            } catch (SocketTimeoutException ex) {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void shutdown() {
        _isRunning.set(false);
    }

    private void chat(Socket socket) {
        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
            try (Scanner scanner = new Scanner(in, "UTF-8");
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));) {
                System.out.println("Chat started.");
                while (true) {
                    String input = scanner.nextLine();
                    if (input != null && Objects.equals(input.toLowerCase(), "bye")) {
                        printWriter.write("bye\n");
                        break;
                    }

                    String output = String.format("Yeah! %s\n", input);
                    printWriter.write(output);
                    printWriter.flush();
                }
            } finally {
                System.out.println("Chat ended.");
            }
        } catch (Exception ex) {
            System.out.println("exception happened in chatting: " + ex);
        }

    }

    @Override
    public void close() throws IOException {
        _chatterThreadPool.close();
        System.out.println("ChatRobot has been closed!");
    }

}
