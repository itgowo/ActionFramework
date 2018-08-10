package com.itgowo.SimpleServerCore.Http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

@Deprecated
public class HttpClient {
    public static void get(String url, String body, HttpServerHandler.onReceiveHandlerListener listener) {
        try {
            connect(url, HttpMethod.GET, body, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String url, String body, HttpServerHandler.onReceiveHandlerListener listener) {
        try {
            connect(url, HttpMethod.POST, body, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connect(String url, HttpMethod method, String body, HttpServerHandler.onReceiveHandlerListener listener) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        if (body == null) {
            body = "";
        }
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, false);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpServerInboundHandlerAdapter().setReceiveHandlerListener(new HttpServerHandler.onReceiveHandlerListener() {
                        @Override
                        public void onReceiveHandler(HttpServerHandler handler) {
                            ChannelHandlerContext ctx=handler.getCtx();
                            if (listener != null) {
                                handler.setCtx(null);
                                listener.onReceiveHandler(handler);
                            }
                            ctx.disconnect();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            if (listener != null) {
                                listener.onError(throwable);
                            }
                        }
                    }));
                }
            });

            URI uri = new URI(url);
            String host = uri.getHost();
            ChannelFuture f = b.connect(host, 80).sync();
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(body.getBytes("UTF-8")));

            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
            // 发送http请求
            f.channel().write(request);
            f.channel().flush();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}