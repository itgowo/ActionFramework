package com.itgowo.servercore.httpclient;

public interface onDownloadFileCallbackListener   {
    void onError(HttpResponse response, Exception e);
    void onProcess(String file, int countBytes, int processBytes);
    void onDownloadFileSuccess(String file);
}
