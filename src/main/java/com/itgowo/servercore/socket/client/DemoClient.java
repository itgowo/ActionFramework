package com.itgowo.servercore.socket.client;

import com.itgowo.servercore.packagesocket.PackageMessage;
import com.itgowo.servercore.socket.ByteBuffer;

import java.net.InetSocketAddress;

public class DemoClient {
    private NioClient nioClient;

    public static void main(String[] args) {
        new DemoClient().testClient();
    }


    public void testClient() {
        nioClient = new NioClient(new InetSocketAddress("localhost", 16671), new onSocketMessageListener() {
            @Override
            public void onConnectedServer() {
                PackageMessage packageMessage = PackageMessage.getPackageMessage().setType(PackageMessage.TYPE_DYNAMIC_LENGTH)
                        .setDataType(PackageMessage.DATA_TYPE_BYTE).setData(new byte[]{11, 22});
                nioClient.sendData(packageMessage.encodePackageMessage());
            }

            @Override
            public void onReadable(ByteBuffer byteBuffer) {

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
