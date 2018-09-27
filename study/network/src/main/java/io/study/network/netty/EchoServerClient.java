package io.study.network.netty;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author koqizhao
 *
 * Sep 27, 2018
 */
public class EchoServerClient implements Closeable {

    public static void main(String[] args) throws InterruptedException, IOException {
        try (EchoServerClient client = new EchoServerClient("localhost", 9090);) {
            client.start();
        }
    }

    private String _server;
    private int _port;

    private Bootstrap _clientBootStrap;

    public EchoServerClient(String server, int port) {
        _server = server;
        _port = port;
    }

    public void start() throws InterruptedException {
        _clientBootStrap = new Bootstrap();
        _clientBootStrap.channel(NioSocketChannel.class).remoteAddress(_server, _port).group(new NioEventLoopGroup(10))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("recieve response: " + msg.toString(Charset.defaultCharset()));
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello, world!", CharsetUtil.UTF_8));
                                super.channelActive(ctx);
                            }
                        });
                    }
                });
        _clientBootStrap.connect().sync().channel().closeFuture().sync();
    }

    @Override
    public void close() throws IOException {
        if (_clientBootStrap != null) {
            try {
                _clientBootStrap.config().group().shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
