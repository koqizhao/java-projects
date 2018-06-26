package io.study.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer implements Closeable {

    public static void main(String[] args) throws IOException {
        try (UdpServer server = new UdpServer(5555);) {
            server.start();
        }
    }

    private int _port;
    private DatagramSocket _socket;

    public UdpServer(int port) {
        _port = port;
    }

    public void start() throws IOException {
        _socket = new DatagramSocket(_port);
        //_socket.setSoTimeout(10 * 1000);

        while (true) {
            DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
            _socket.receive(request);
            System.out.println("request: " + new String(request.getData()));

            DatagramPacket response = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
                    request.getPort());
            _socket.send(response);
            System.out.println("response: " + new String(response.getData()));
        }
    }

    @Override
    public void close() throws IOException {
        if (_socket != null)
            _socket.close();
    }

}
