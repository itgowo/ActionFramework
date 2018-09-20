package com.itgowo.servercore;

public class Test {
    public static void main(String[] args) {
        try {
//            SimpleServer.testSocketServer();
//            HttpClient.get("http://stzb.163.com/herolist/100006.html",null, new WebSocketServerHandler.onReceiveHandlerListener() {
//                @Override
//                public void onReceiveHandler(WebSocketServerHandler handler) {
//                    ServerManager.getLogger().info(handler.getBody(Charset.forName("gb2312")));
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            });
            SimpleServer.testSocketServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
