package io.study.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author koqizhao
 *
 * Sep 26, 2018
 */
public class MulticastServer implements Closeable {

    public static final int MAX_PACKET_SIZE = 65507;
    public static final String NETWORK_INTERFACE_NAME = "vboxnet0";
    // public static final String GROUP_ADDRESS = "255.255.255.255"; // broadcast
    public static final String GROUP_ADDRESS = "225.0.0.0"; // multicast
    public static final int GROUP_PORT = 8889;

    public static void main(String[] args) throws IOException, InterruptedException {
        try (MulticastServer server = new MulticastServer(8888, GROUP_ADDRESS, GROUP_PORT)) {
            server.start();
        }
    }

    public static void showNetworkInterfaces() throws SocketException {
        Enumeration<NetworkInterface> enumInterfaces = NetworkInterface.getNetworkInterfaces();
        while (enumInterfaces.hasMoreElements()) {
            NetworkInterface net = enumInterfaces.nextElement();
            System.out.println("Network Interface Display Name: " + net.getDisplayName());
            System.out.println(net.getDisplayName() + " is up and running ?" + net.isUp());
            System.out.println(net.getDisplayName() + " Supports Multicast: " + net.supportsMulticast());
            System.out.println(net.getDisplayName() + " Name: " + net.getName());
            System.out.println(net.getDisplayName() + " Is Virtual: " + net.isVirtual());
            System.out.println("IP addresses:");
            Enumeration<InetAddress> enumIP = net.getInetAddresses();
            while (enumIP.hasMoreElements()) {
                InetAddress ip = enumIP.nextElement();
                System.out.println("IP address:" + ip);
            }

            System.out.println("\n");
        }
    }

    public static NetworkInterface getNetworkInterfaceForMulticast(String networkInterfaceName) throws SocketException {
        return NetworkInterface.getByName(networkInterfaceName);
    }

    public static NetworkInterface getNetworkInterfaceForMulticast() throws SocketException {
        Enumeration<NetworkInterface> enumInterfaces = NetworkInterface.getNetworkInterfaces();
        while (enumInterfaces.hasMoreElements()) {
            NetworkInterface net = enumInterfaces.nextElement();
            if (net.supportsMulticast())
                return net;
        }

        throw new IllegalStateException("No multicast network interface on the host");
    }

    private AtomicBoolean _isRunning;

    private int _port;
    private DatagramChannel _serverChannel;
    private Selector _selector;

    private String _targetGroup;
    private int _targetPort;

    private Thread _reciever;

    public MulticastServer(int port, String targetGroup, int targetPort) {
        _port = port;
        _targetGroup = targetGroup;
        _targetPort = targetPort;
        _isRunning = new AtomicBoolean();
    }

    public void start() throws IOException, InterruptedException {
        if (!_isRunning.compareAndSet(false, true))
            return;

        _serverChannel = DatagramChannel.open(StandardProtocolFamily.INET);
        if (!_serverChannel.isOpen())
            throw new IllegalStateException("Cannot open a server channel.");

        _serverChannel.configureBlocking(false);
        _serverChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF,
                getNetworkInterfaceForMulticast(NETWORK_INTERFACE_NAME));
        _serverChannel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 2);
        _serverChannel.setOption(StandardSocketOptions.SO_BROADCAST, true);
        _serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        _serverChannel.bind(new InetSocketAddress("192.168.56.1", _port));

        _selector = Selector.open();
        _serverChannel.register(_selector, SelectionKey.OP_READ);

        _reciever = new Thread(this::showRecieved);
        _reciever.setDaemon(true);
        _reciever.setName("reciever thread");
        _reciever.start();

        InetSocketAddress target = new InetSocketAddress(_targetGroup, _targetPort);
        for (long i = 0; _isRunning.get(); i++) {
            byte[] data = ("Hello, world! " + i).getBytes();
            _serverChannel.send(ByteBuffer.wrap(data), target);

            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        }
    }

    private void showRecieved() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        while (_isRunning.get()) {
            try {
                _selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Iterator<SelectionKey> selectedKeys = _selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey selectionKey = selectedKeys.next();
                selectedKeys.remove();

                if (!selectionKey.isValid())
                    continue;

                if (selectionKey.isReadable()) {
                    DatagramChannel channel = (DatagramChannel) selectionKey.channel();
                    SocketAddress remoteAddress;
                    try {
                        remoteAddress = channel.receive(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    buffer.flip();
                    String data = null;
                    try {
                        CharBuffer charBuffer = decoder.decode(buffer);
                        data = charBuffer.toString();
                    } catch (CharacterCodingException e) {
                        e.printStackTrace();
                    }

                    buffer.clear();

                    System.out.printf("\nGot a response: { remote: %s, data: %s }\n", remoteAddress, data);

                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        _isRunning.set(false);

        if (_serverChannel != null)
            _serverChannel.close();

        if (_selector != null)
            _selector.close();

        if (_reciever != null)
            _reciever.interrupt();
    }
}
