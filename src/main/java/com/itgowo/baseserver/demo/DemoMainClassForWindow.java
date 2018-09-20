package com.itgowo.baseserver.demo;

import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.baseserver.ServerManager;
import com.itgowo.baseserver.base.BaseConfig;
import com.itgowo.baseserver.base.Dispatcher;
import com.itgowo.baseserver.base.HttpServerInitCallback;

public class DemoMainClassForWindow implements HttpServerInitCallback {

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
}
