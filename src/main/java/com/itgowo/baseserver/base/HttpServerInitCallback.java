package com.itgowo.baseserver.base;

import com.itgowo.servercore.http.HttpServerManager;

/**
 * 使用界面控制服务，启动服务类需要实现此接口
 */
public interface HttpServerInitCallback {
    void onServerConfigPrepare(HttpServerManager serverManager);

    void onError(Exception e);
}
