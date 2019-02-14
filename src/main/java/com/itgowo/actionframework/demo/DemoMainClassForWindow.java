package com.itgowo.actionframework.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.BaseConfig;
import com.itgowo.actionframework.base.Dispatcher;
import com.itgowo.actionframework.base.HttpServerInitCallback;
import com.itgowo.actionframework.base.onJsonConvertListener;
import com.itgowo.servercore.http.HttpServerManager;

public class DemoMainClassForWindow implements HttpServerInitCallback, onJsonConvertListener<DemoClientRequest> {

    @Override
    public void onServerConfigPrepare(HttpServerManager httpServerManager) {
        Dispatcher dispatcher = new Dispatcher();
        httpServerManager.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
//        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
//        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
//        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
//        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
        if (BaseConfig.isAnalysisTps()) dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.actionScanner(DemoMainClassForWindow.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        ServerManager.setOnJsonConvertListener( this);
        httpServerManager.setOnServerListener(dispatcher);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        ServerManager.onError(e);
    }

    @Override
    public DemoClientRequest parseJson(String string) throws Exception {
        return JSON.parseObject(string, DemoClientRequest.class);
    }

    @Override
    public <T> T parseJson(String string, Class<T> tClass) throws Exception {
        return JSON.parseObject(string,tClass);
    }

    @Override
    public String toJson(Object o) throws Exception {
        return JSON.toJSONString(o);
    }
}
