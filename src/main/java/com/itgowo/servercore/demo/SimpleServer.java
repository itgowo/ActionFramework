package com.itgowo.servercore.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.servercore.onServerListener;
import com.itgowo.servercore.packagesocket.PackageMessage;
import com.itgowo.servercore.packagesocket.PackageServerHandler;
import com.itgowo.servercore.packagesocket.PackageServerManager;
import com.itgowo.servercore.socket.SocketServerHandler;
import com.itgowo.servercore.socket.SocketServerManager;
import com.itgowo.servercore.websocket.WebSocketServerHandler;
import com.itgowo.servercore.websocket.WebSocketServerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SimpleServer {
    public static void testHttpServer() throws Exception {
        HttpServerManager httpServerManager = new HttpServerManager("web","simpleHttpServer");
        httpServerManager.setThreadConfig(2, 4);
        httpServerManager.setOnServerListener(new onServerListener<HttpServerHandler>() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {

            }

            @Override
            public void onChannelInactive(ChannelHandlerContext ctx) {

            }

            @Override
            public void onReceiveHandler(HttpServerHandler handler) throws Exception {
//                System.out.println(handler);
//                handler.sendRedirect("http://baidu.com");
                String path = handler.getPath();
                if (handler.getHttpRequest().method() == HttpMethod.OPTIONS) {
                    handler.sendOptionsResult();
                } else if (handler.getHttpRequest().method() == HttpMethod.GET) {
                    if (path.equalsIgnoreCase("")) {
                        path = "/index.html";
                    }
                    handler.sendFile(new File("/Users/lujianchao/WebstormProjects/untitled1", path), true);
                } else {
                    try {
                        Thread.sleep(1000);
                        handler.sendData("ok" + handler.getUri(), false);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onUserEventTriggered(Object event) {

            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onServerStarted(int serverPort) {

            }

            @Override
            public void onServerStop() {

            }
        });
        httpServerManager.prepare(12000);
        httpServerManager.start();
    }

    public static void testSocketServer() throws Exception {
        SocketServerManager socketServerManager = new SocketServerManager("testSocketServer");
        socketServerManager.setThreadConfig(2, 4);
        socketServerManager.setOnServerListener(new onServerListener<SocketServerHandler>() {
            @Override
            public void onReceiveHandler(SocketServerHandler handler) {
                System.out.println("收到消息" + handler);
                System.out.println("发送消息");
                handler.sendData("啦啦啦".getBytes());
            }

            @Override
            public void onUserEventTriggered(Object event) {

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

            @Override
            public void onServerStarted(int serverPort) {

            }

            @Override
            public void onServerStop() {

            }
        });
        socketServerManager.prepare(12001);
        socketServerManager.start();
    }

    public static void testWebSocketServer() throws Exception {
        WebSocketServerManager manager = new WebSocketServerManager("testWebSocketServer");
        manager.setThreadConfig(1, 1);
        manager.setOnServerListener(new onServerListener<WebSocketServerHandler>() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                System.out.println("SimpleServer.onChannelActive");
            }

            @Override
            public void onChannelInactive(ChannelHandlerContext ctx) {
                System.out.println("SimpleServer.onChannelInactive");
            }

            @Override
            public void onReceiveHandler(WebSocketServerHandler handler) throws Exception {
                System.out.println("SimpleServer.onReceiveHandler " + handler.toString());
                handler.sendResponse(new TextWebSocketFrame(handler.getWebSocketFrame().toString()));
            }

            @Override
            public void onUserEventTriggered(Object event) {

            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onServerStarted(int serverPort) {
                System.out.println("SimpleServer.onServerStarted");
            }

            @Override
            public void onServerStop() {
                System.out.println("SimpleServer.onServerStop");
            }
        });
        manager.setSSL(false).setWebsocketPath("");
        manager.prepare(1221);
        manager.start();
    }

    public static void testPackageServer() throws Exception {
        PackageServerManager packageServerManager = new PackageServerManager("testPackageServer");
        packageServerManager.setThreadConfig(2, 4);
        packageServerManager.setOnServerListener(new onServerListener<PackageServerHandler>() {
            @Override
            public void onReceiveHandler(PackageServerHandler handler) {
                if (handler.getPackageMessage().getDataType() == PackageMessage.DATA_TYPE_HEART) {
                    System.out.println("糟了，是心动的感觉！");
                } else {
                    System.out.println("收到消息" + handler);
                }
                handler.sendData(handler.getPackageMessage());
            }

            @Override
            public void onUserEventTriggered(Object event) {

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

            @Override
            public void onServerStarted(int serverPort) {

            }

            @Override
            public void onServerStop() {

            }
        });
        packageServerManager.prepare(12002);
        packageServerManager.start();
    }

}
