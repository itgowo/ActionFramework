package com.itgowo.servercore.socket.client;

import com.itgowo.servercore.packagesocket.PackageMessage;
import com.itgowo.servercore.socket.ByteBuffer;

import javax.activation.MimetypesFileTypeMap;
import java.net.InetSocketAddress;

public class DemoClient {
    private NioClient nioClient;

    public static void main(String[] args) {
        new DemoClient().testClient();
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
       String s= mimetypesFileTypeMap.getContentType("aaa.js");
        System.out.println(s);
    }


    public void testClient() {
        nioClient = new NioClient(new InetSocketAddress("www.baidu.com", 80), new onSocketMessageListener() {
            @Override
            public void onConnectedServer() {
//                PackageMessage packageMessage = PackageMessage.getPackageMessage().setType(PackageMessage.TYPE_DYNAMIC_LENGTH)
//                        .setDataType(PackageMessage.DATA_TYPE_BYTE).setData(new byte[]{11, 22});
//                nioClient.sendData(packageMessage.encodePackageMessage());


                String s="OPTIONS / HTTP/1.1\n" +
                        "cache-control: no-cache\n" +
                        "User-Agent: PostmanRuntime/7.2.0\n" +
                        "Accept: */*\n" +
                        "accept-encoding: gzip, deflate\n" +
                        "Connection: keep-alive\r\n\r\n";
                nioClient.sendBytes(s.getBytes());
            }

            @Override
            public void onReadable(ByteBuffer byteBuffer) {
                System.out.println(new String(byteBuffer.readableBytesArray()));
            }

            @Override
            public void onError(String errormsg, Exception e) {

            }

            @Override
            public void onStop() {

            }
        });
        nioClient.start();
    }
}
