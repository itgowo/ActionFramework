package com.itgowo.servercore.httpclient;

public interface onUploadFileCallbackListener extends onCallbackListener {
    void onProcess(String file, int countBytes, int processBytes);
}
