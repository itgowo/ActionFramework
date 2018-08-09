package com.itgowo.baseServer.base;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;

/**
 * 事件分发处理，将实现该接口的类添加到处理器中就会被自动事件调用
 */
public interface ActionRequest {
    /**
     * 只处理POST请求，只有匹配对应action才会触发
     *
     * @param handler
     * @param baseRequest
     * @throws Exception
     */
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception;

}
