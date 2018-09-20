package com.itgowo.actionserver.base;

import com.itgowo.servercore.http.HttpServerManager;

/**
 * 使用界面控制服务，启动服务类需要实现此接口
 */
public interface HttpServerInitCallback<Request extends BaseRequest> {
    void onServerConfigPrepare(HttpServerManager serverManager);

    void onError(Exception e);

    /**
     * 定义Json解析器
     *
     * @param string
     * @return 返回指定对象
     * @throws Exception
     */
    public Request parseJson(String string) throws Exception;

    /**
     * 生成json序列化文本
     *
     * @param o
     * @return
     * @throws Exception
     */
    public String toJson(Object o) throws Exception;


}
