package io.study.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpClient implements Closeable {

    public static void main(String[] args) throws IOException {
        try (UdpClient client = new UdpClient(5555);) {
            client.Start();
        }
    }

    private int _serverPort;
    private DatagramSocket _socket;

    public UdpClient(int serverPort) {
        _serverPort = serverPort;
    }

    public void Start() throws IOException {
        _socket = new DatagramSocket();
        //_socket.setSoTimeout(10 * 1000);

        int i = 0;
        while (true) {
            byte[] data = ("Hello " + Integer.toString(i++)).getBytes();
            DatagramPacket request = new DatagramPacket(data, data.length, new InetSocketAddress(_serverPort));
            _socket.send(request);
            System.out.println("request: " + new String(request.getData()));

            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
            _socket.receive(response);
            System.out.println("response: " + new String(response.getData()));

            if (i == 10)
                break;
        }
    }

    @Override
    public void close() throws IOException {
        if (_socket != null)
            _socket.close();
    }

}
