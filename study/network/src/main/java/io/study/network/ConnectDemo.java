package io.study.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

public class ConnectDemo {

    public static void main(String[] args) throws IOException {
        testSocketChannelNonBlocking();
    }

    public static void testConnect() throws UnknownHostException, IOException {
        try (Socket socket = new Socket("time-a.nist.gov", 13); Scanner in = new Scanner(socket.getInputStream())) {
            while (in.hasNextLine())
                System.out.println(in.nextLine());
        }
    }

    public static void testInetAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getByName("128.0.0.1");
        for (byte b : address.getAddress())
            System.out.println(b);

        InetAddress[] addresses = InetAddress.getAllByName("qiang-mac-ubuntu");
        System.out.println(Arrays.asList(addresses));

        address = InetAddress.getLocalHost();
        System.out.println(address);
    }

    public static void testServerSocket() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080);) {
            System.out.println("Server started at port: 8080");
            try (Socket socket = serverSocket.accept();) {
                System.out.println("a connection established: " + socket);
                try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
                    try (Scanner scanner = new Scanner(in, "UTF-8");
                            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));) {
                        while (true) {
                            String input = scanner.nextLine();
                            if (Objects.equals(input, "bye")) {
                                printWriter.write("bye\n");
                                break;
                            }

                            String output = String.format("Yeah! %s\n", input);
                            printWriter.write(output);
                            printWriter.flush();
                        }
                    }
                }
            }
        }
    }

    public static void testChatRobot() throws IOException {
        try (ChatRobot chatRobot = new ChatRobot(8080);) {
            chatRobot.start();
        }
    }

    public static void testNetworkChannel() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        try (ServerSocketChannel channel = ServerSocketChannel.open();) {
            if (!channel.isOpen()) {
                System.out.println("server channel cannot be opened");
                return;
            }

            System.out.println(channel.supportedOptions());
            System.out.println();

            //channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.bind(new InetSocketAddress(8080));

            System.out.println("Waiting for connections...");
            while (true) {
                try (SocketChannel socketChannel = channel.accept();) {
                    SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                    SocketAddress localAddress = socketChannel.getLocalAddress();
                    System.out.printf("local: %s, remote: %s", localAddress, remoteAddress);
                    while (socketChannel.read(byteBuffer) != -1) {
                        byteBuffer.flip();

                        socketChannel.write(byteBuffer);
                        if (byteBuffer.hasRemaining())
                            byteBuffer.compact();
                        else
                            byteBuffer.clear();
                    }
                }
            }
        }
    }

    public static void testSocketChannel() throws IOException {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.supportedOptions().forEach(System.out::println);

            channel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            channel.setOption(StandardSocketOptions.SO_LINGER, 5);

            channel.connect(new InetSocketAddress(8080));

            ByteBuffer helloBuffer = ByteBuffer.wrap("Hello!\n".getBytes());
            channel.write(helloBuffer);

            Charset charset = Charset.forName("UTF-8");
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int i = 0;
            while (channel.read(buffer) != -1) {
                buffer.flip();

                CharBuffer charBuffer = charset.decode(buffer);
                System.out.println("in: " + charBuffer);

                if (buffer.hasRemaining())
                    buffer.compact();
                else
                    buffer.clear();

                channel.write(ByteBuffer.wrap(("return: " + i + "\n").getBytes()));

                if (i++ == 10)
                    break;
            }
        }
    }

    public static void testSocketChannelNonBlocking() throws IOException {
        try (SocketChannel channel = SocketChannel.open(); Selector selector = Selector.open()) {
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            channel.setOption(StandardSocketOptions.SO_LINGER, 5);
            channel.connect(new InetSocketAddress(5555));
            channel.register(selector, SelectionKey.OP_CONNECT);

            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            Charset charset = Charset.forName("UTF-8");
            int i = 0;
            while (true) {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey selectionKey = selectedKeys.next();
                    selectedKeys.remove();

                    if (!selectionKey.isValid())
                        continue;

                    if (selectionKey.isConnectable()) {
                        if (channel.isConnectionPending())
                            channel.finishConnect();
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                    } else if (selectionKey.isReadable()) {
                        while (channel.read(buffer) > 0) {
                            buffer.flip();

                            CharBuffer charBuffer = charset.decode(buffer);
                            System.out.println("in: " + charBuffer);

                            if (buffer.hasRemaining())
                                buffer.compact();
                            else
                                buffer.clear();
                        }

                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                    } else if (selectionKey.isWritable()) {
                        channel.write(ByteBuffer.wrap(("return: " + i + "\n").getBytes()));

                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }
                }

                if (i++ == 10)
                    break;
            }
        }
    }

}
