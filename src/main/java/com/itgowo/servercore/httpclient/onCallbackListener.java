package com.itgowo.servercore.httpclient;

public interface onCallbackListener {
    void onError(Response response, Exception e);

    void onSuccess(Response response);
}
