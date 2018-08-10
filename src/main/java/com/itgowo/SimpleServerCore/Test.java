package com.itgowo.SimpleServerCore;

public class Test {
    public static void main(String[] args) {
        try {
//            SimpleServer.testSocketServer();
//            HttpClient.get("http://stzb.163.com/herolist/100006.html",null, new HttpServerHandler.onReceiveHandlerListener() {
//                @Override
//                public void onReceiveHandler(HttpServerHandler handler) {
//                    System.out.println(handler.getBody(Charset.forName("gb2312")));
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            });

        SimpleServer.testHttpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
