package io.study.network.netty;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author koqizhao
 *
 * Sep 27, 2018
 */
public class EchoServer implements Closeable {

    public static void main(String[] args) throws InterruptedException, IOException {
        try (EchoServer echoServer = new EchoServer(9090);) {
            echoServer.start();
        }
    }

    private int _port;
    private ServerBootstrap _serverBootstrap;

    public EchoServer(int port) {
        _port = port;
    }

    public void start() throws InterruptedException {
        _serverBootstrap = new ServerBootstrap();
        _serverBootstrap.channel(NioServerSocketChannel.class).localAddress(_port)
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(10))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                System.out.printf("\nGot a request: %s\n", byteBuf.toString(Charset.defaultCharset()));
                                ctx.writeAndFlush(msg);
                            }

                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                super.channelReadComplete(ctx);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                            }
                        });
                    }
                });
        _serverBootstrap.bind().sync().channel().closeFuture().sync();
        System.out.println("server started");
    }

    @Override
    public void close() throws IOException {
        if (_serverBootstrap != null) {
            try {
                _serverBootstrap.config().group().shutdownGracefully().sync();
                _serverBootstrap.config().childGroup().shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("server closed");
        }
    }

}
