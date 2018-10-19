package com.itgowo.servercore;

import com.itgowo.socketframework.PackageMessage;

import java.util.Arrays;

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
//            SimpleServer.testHttpServer();
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

        int result = PackageMessage.dataSign(new byte[]{33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56});
        System.out.println(Arrays.toString(PackageMessage.intToByteArray(result)));
    }

    public static int byteArrayToInt(byte[] b) {
        if (b == null) {
            return 0;
        }
        if (b.length == 3) {
            return (b[2] & 0xFF) | (b[1] & 0xFF) << 8 | (b[0] & 0xFF) << 16;
        }
        if (b.length == 2) {
            return (b[1] & 0xFF) | (b[0] & 0xFF) << 8;
        }
        if (b.length == 1) {
            return b[0] & 0xFF;
        }
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }
}
