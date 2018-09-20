package com.itgowo.actionserver.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.actionserver.ServerManager;
import com.itgowo.actionserver.base.BaseConfig;
import com.itgowo.actionserver.base.Dispatcher;
import com.itgowo.actionserver.base.HttpServerInitCallback;

public class DemoMainClassForWindow implements HttpServerInitCallback<DemoClientRequest> {

    @Override
    public void onServerConfigPrepare(HttpServerManager serverManager) {
        Dispatcher dispatcher = new Dispatcher();
        serverManager.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
        if (BaseConfig.isAnalysisTps())dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.actionScanner(DemoMainClassForWindow.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        serverManager.setOnServerListener(dispatcher);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        ServerManager.onError(e);
    }

    @Override
    public DemoClientRequest parseJson(String string) throws Exception {
        return JSON.parseObject(string,DemoClientRequest.class);
    }

    @Override
    public String toJson(Object o) throws Exception {
        return JSON.toJSONString(o);
    }
}
