package com.itgowo.servercore.socket.client;

import com.itgowo.servercore.socket.ByteBuffer;

public interface onSocketMessageListener {
    void onConnectedServer();

    void onReadable(ByteBuffer byteBuffer);

    void onError(String errormsg, Exception e);

    void onStop();
}
