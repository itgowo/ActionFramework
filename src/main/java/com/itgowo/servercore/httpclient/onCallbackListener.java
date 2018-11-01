package com.itgowo.servercore.httpclient;

public interface onCallbackListener {
    void onError(HttpResponse response, Exception e);

    void onSuccess(HttpResponse response);
}
