package io.study.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author koqizhao
 *
 * Sep 26, 2018
 */
@SuppressWarnings("unused")
public class MulticastClient {

    public static void main(String[] args) throws IOException {
        // serveBroadcast();
        serveMulticast();
    }

    private static void serveBroadcast() throws IOException {
        DatagramChannel clientChannel = DatagramChannel.open(StandardProtocolFamily.INET);
        clientChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        clientChannel.bind(new InetSocketAddress(MulticastServer.GROUP_PORT));
        ByteBuffer buffer = ByteBuffer.allocateDirect(MulticastServer.MAX_PACKET_SIZE);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        for (long i = 0; true; i++) {
            SocketAddress remoteAddress = clientChannel.receive(buffer);
            buffer.flip();
            CharBuffer charBuffer = decoder.decode(buffer);
            String data = charBuffer.toString();
            System.out.printf("\nclient: got message from %s, data: %s\n", remoteAddress, data);

            if (i % 10 == 0) {
                clientChannel.send(ByteBuffer.wrap(data.getBytes()), remoteAddress);
            }

            buffer.clear();
        }
    }

    private static void serveMulticast() throws IOException {
        DatagramChannel clientChannel = DatagramChannel.open(StandardProtocolFamily.INET);
        clientChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        clientChannel.bind(new InetSocketAddress(MulticastServer.GROUP_PORT));
        InetAddress group = InetAddress.getByName(MulticastServer.GROUP_ADDRESS);
        MembershipKey membershipKey = clientChannel.join(group,
                MulticastServer.getNetworkInterfaceForMulticast(MulticastServer.NETWORK_INTERFACE_NAME));
        ByteBuffer buffer = ByteBuffer.allocateDirect(MulticastServer.MAX_PACKET_SIZE);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        for (long i = 0; true; i++) {
            if (!membershipKey.isValid())
                break;

            SocketAddress remoteAddress = clientChannel.receive(buffer);
            buffer.flip();
            CharBuffer charBuffer = decoder.decode(buffer);
            String data = charBuffer.toString();
            buffer.clear();
            System.out.printf("\nclient: got message from %s, data: %s\n", remoteAddress, data);

            if (i % 10 == 0) {
                clientChannel.send(ByteBuffer.wrap(data.getBytes()), remoteAddress);
            }
        }
    }

}
