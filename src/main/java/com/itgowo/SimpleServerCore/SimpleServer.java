package com.itgowo.SimpleServerCore;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.SimpleServerCore.Http.HttpServerManager;
import com.itgowo.SimpleServerCore.Socket.SocketServerHandler;
import com.itgowo.SimpleServerCore.Socket.SocketServerManager;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

public class SimpleServer {
    public static void testHttpServer() throws Exception {
        HttpServerManager httpServerManager = new HttpServerManager();
        httpServerManager.setThreadConfig(2,4);
        httpServerManager.setOnReceiveHandleListener(new HttpServerHandler.onReceiveHandlerListener() {
            @Override
            public void onReceiveHandler(HttpServerHandler handler) {
                System.out.println(handler);
                try {
                    handler.sendData("我收到了", false);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        httpServerManager.start(12000);
    }

    public static void testSocketServer() throws Exception {
        SocketServerManager socketServerManager = new SocketServerManager();
        socketServerManager.setThreadConfig(2,4);
        socketServerManager.setOnReceiveHandleListener(new SocketServerHandler.onReceiveHandlerListener() {
            @Override
            public void onReceiveHandler(SocketServerHandler handler) {
                System.out.println("收到消息" + handler);
                System.out.println("发送消息");
                handler.sendData("啦啦啦".getBytes());

            }

            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                System.out.println("SimpleServer.onChannelActive  有新客户端连接，可以交流发送数据");
            }

            @Override
            public void onChannelInactive(ChannelHandlerContext ctx) {
                System.out.println("SimpleServer.onChannelInactive  有客户端失联了");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        socketServerManager.start(12001);
    }
}
