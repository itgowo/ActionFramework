package com.itgowo.servercore.socket.client;

import com.itgowo.servercore.socket.ByteBuffer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient extends Thread {
    private SocketChannel socketChannel;
    private SocketAddress socketAddress;
    private onSocketMessageListener onSocketMessageListener;
    private boolean isRunning;

    public NioClient(SocketAddress socketAddress, com.itgowo.servercore.socket.client.onSocketMessageListener onSocketMessageListener) {
        this.socketAddress = socketAddress;
        this.onSocketMessageListener = onSocketMessageListener;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public void sendBytes(byte[] bytes) {
        sendData(java.nio.ByteBuffer.wrap(bytes));
    }

    public void sendData(java.nio.ByteBuffer byteBuffer) {
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            onSocketMessageListener.onError("发送失败", e);
        }
    }

    public void sendData(ByteBuffer byteBuffer) {
        sendData(java.nio.ByteBuffer.wrap(byteBuffer.readableBytesArray()));
    }

    public void run() {
        super.run();
        Selector selector = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(socketAddress);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            isRunning = true;
            while (isRunning) {
                selector.select();
                Iterator ite = selector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = (SelectionKey) ite.next();
                    ite.remove();
                    if (key.isConnectable()) {
                        if (socketChannel.isConnectionPending()) {
                            if (socketChannel.finishConnect()) {
                                //只有当连接成功后才能注册OP_READ事件
                                key.interestOps(SelectionKey.OP_READ);
                                onSocketMessageListener.onConnectedServer();
                            } else {
                                key.cancel();
                                onSocketMessageListener.onError("连接失败！", new Exception("requestConnect：cancel"));
                                onSocketMessageListener.onStop();
                            }
                        }
                    } else if (key.isReadable()) {
                        java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(256);
                        int count = socketChannel.read(byteBuffer);
                        byteBuffer.flip();
                        if (count == -1) {
                            stopServer();
                        } else if (count != 0) {
                            ByteBuffer buffer = ByteBuffer.newByteBuffer();
                            buffer.writeBytes(byteBuffer.array(), byteBuffer.remaining());
                            onSocketMessageListener.onReadable(buffer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            isRunning = false;
            e.printStackTrace();
            onSocketMessageListener.onError("remote error,远程连接失败", e);
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() throws IOException {
        isRunning = false;
        if (socketChannel != null) {
            socketChannel.close();
        }
        onSocketMessageListener.onStop();
    }
}
