package com.itgowo.baseServer.demo;

import com.itgowo.SimpleServerCore.Http.HttpServerManager;
import com.itgowo.baseServer.ServerManager;
import com.itgowo.baseServer.Test;
import com.itgowo.baseServer.base.BaseConfig;
import com.itgowo.baseServer.base.Dispatcher;
import com.itgowo.baseServer.base.HttpServerInitCallback;

public class DemoMainClassForWindow implements HttpServerInitCallback {

    @Override
    public void onServerConfigPrepare(HttpServerManager serverManager) {
        Dispatcher dispatcher = new Dispatcher();
        serverManager.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
        dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.actionScanner(Test.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        serverManager.setOnReceiveHandleListener(dispatcher);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        ServerManager.onError(e);
    }
}
