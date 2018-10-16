package com.itgowo.servercore;

import com.itgowo.servercore.httpclient.HttpClient;
import com.itgowo.servercore.httpclient.HttpMethod;
import com.itgowo.servercore.httpclient.Response;
import com.itgowo.servercore.httpclient.onCallbackListener;

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
            SimpleServer.testHttpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Response response= HttpClient.RequestSync("localhost:16671",HttpMethod.POST,"asdfljasldf");
//            System.out.println(response.getBodaStr());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        HttpClient.RequestPOST("localhost:16671", "abacvasdfasewrjowriuwo", new onCallbackListener() {
//            @Override
//            public void onError(Response response, Exception e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(Response response) {
//                System.out.println(response.getBodaStr());
//            }
//        });
    }
}
