package com.itgowo.servercore;

import com.itgowo.servercore.socket.client.DemoClient;

public class Test {
    public static void main(String[] args) {


        try {
//            SimpleServer.testSocketServer();
//            HttpClient.get("http://stzb.163.com/herolist/100006.html",null, new WebSocketServerHandler.onReceiveHandlerListener() {
//                @Override
//                public void onReceiveHandler(WebSocketServerHandler handler) {
//                    BaseServerManager.getLogger().info(handler.getBody(Charset.forName("gb2312")));
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            });
//            SimpleServer.testPacgageServer();
            DemoClient.main(null);
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

//        byte[] bytes = new byte[]{33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56};
//        ByteBuf byteBuf = Unpooled.buffer(40);
//        byteBuf.writeBytes(bytes);
//        PackageMessage packageMessage = PackageMessage.getPackageMessage();
//        packageMessage.setType(PackageMessage.TYPE_DYNAMIC_LENGTH).setDataType(PackageMessage.DATA_TYPE_BYTE).setData(byteBuf);
//        ByteBuf byteBuf1 = packageMessage.encodePackageMessage();
//        List<PackageMessage> packageMessage1=PackageMessage.packageMessage(byteBuf1);

//        byte[] test1 = new byte[]{
//                121};
//        List<PackageMessage> packageMessage1 = PackageMessage.packageMessage(ByteBuffer.newByteBuffer().writeBytes(test1));
//        byte[] test2 = new byte[]{
//                0, 0, 0, 24,
//                3, 3, 44, 10,
//                23,
//                33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56
//
//                //下面是第二个数据包的数据，模拟黏包拆包操作
//                , 121,
//                0, 0, 0, 24,
//                3, 3, 44, 10
//                //模拟半包发送
////                ,23,
////                33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56
//        };
//        List<PackageMessage> packageMessage2 = PackageMessage.packageMessage(ByteBuffer.newByteBuffer().writeBytes(test2));
//        byte[] test3 = new byte[]{
//                23,
//                33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56
//                //下面是第三个数据包
//                , 121
//                , 0, 0, 0, 24,
//                3, 3, 44, 10,
//                23,
//                33, 22, 11, 44, 55, 66, 77, 88, 99, 11, 23, 34, 45, 56};
//        List<PackageMessage> packageMessage3 = PackageMessage.packageMessage(ByteBuffer.newByteBuffer().writeBytes(test3));
//        System.out.println("ddd");
//
//        byte[] aaa1 = new byte[]{11, 12};
//        byte[] aaa2 = new byte[]{22, 33, 44, 55, 66, 77};
//        ByteBuffer buffer = ByteBuffer.newByteBuffer(2);
//        try {
//            buffer.writeBytes(aaa1);
//            System.out.println("capacity:" + buffer.capacity() + "  readableBytes:" + buffer.readableBytes() + "   writableBytes:" + buffer.writableBytes());
//            buffer.writeBytes(aaa2);
//            System.out.println("capacity:" + buffer.capacity() + "  readableBytes:" + buffer.readableBytes() + "   writableBytes:" + buffer.writableBytes());
//            int i1 = buffer.read();
//            System.out.println("capacity:" + buffer.capacity() + "  readableBytes:" + buffer.readableBytes() + "   writableBytes:" + buffer.writableBytes());
//            int i2 = buffer.readInt();
//            System.out.println("capacity:" + buffer.capacity() + "  readableBytes:" + buffer.readableBytes() + "   writableBytes:" + buffer.writableBytes());
//            byte[] b1 = new byte[3];
//            buffer.readBytes(b1);
//            System.out.println("capacity:" + buffer.capacity() + "  readableBytes:" + buffer.readableBytes() + "   writableBytes:" + buffer.writableBytes());
//
//        } catch (ByteBuffer.ByteBufferException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(Arrays.toString(aaa1));
//        System.out.println(Arrays.toString(aaa2));
    }

}
