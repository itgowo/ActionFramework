package com.itgowo.baseServer.base;

import com.itgowo.SimpleServerCore.Http.HttpServerManager;

/**
 * 使用界面控制服务，启动服务类需要实现此接口
 */
public interface HttpServerInitCallback {
    void onServerConfigPrepare(HttpServerManager serverManager);

    void onError(Exception e);
}
