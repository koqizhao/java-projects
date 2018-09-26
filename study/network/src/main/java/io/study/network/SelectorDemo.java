package io.study.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * @author koqizhao
 *
 * Jun 26, 2018
 */
public class SelectorDemo {

    public static void main(String[] args) throws IOException {
        SelectorDemo selectorDemo = new SelectorDemo();
        selectorDemo.startEchoServer();
    }

    private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

    private void startEchoServer() throws IOException {
        final int DEFAULT_PORT = 5555;
        try (Selector selector = Selector.open();
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            if (!selector.isOpen() || !serverSocketChannel.isOpen())
                return;

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocketChannel.bind(new InetSocketAddress(DEFAULT_PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("waiting for connections...");

            while (true) {
                selector.select();

                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable())
                        acceptOP(key, selector);
                    else if (key.isReadable())
                        readOP(key);
                    else if (key.isWritable())
                        writeOP(key);
                }
            }
        }
    }

    private void acceptOP(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("Incoming connection from: " + socketChannel.getRemoteAddress());

        socketChannel.write(ByteBuffer.wrap("Hello!\n".getBytes("UTF-8")));

        keepDataTrack.put(socketChannel, new ArrayList<>());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void readOP(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int numRead = -1;
        try {
            numRead = socketChannel.read(buffer);
        } catch (IOException e) {
            System.err.println("Cannot read error: " + e);
        }

        if (numRead == -1) {
            keepDataTrack.remove(socketChannel);
            System.out.println("Connection closed by: " + socketChannel.getRemoteAddress());
            socketChannel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, data.length);
        System.out.println(new String(data, "UTF-8") + " from " + socketChannel.getRemoteAddress());

        doEchoJob(key, data);
    }

    private void writeOP(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        Iterator<byte[]> its = channelData.iterator();
        while (its.hasNext()) {
            byte[] it = its.next();
            its.remove();
            socketChannel.write(ByteBuffer.wrap(it));
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void doEchoJob(SelectionKey key, byte[] data) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);
        key.interestOps(SelectionKey.OP_WRITE);
    }

}
