package com.itgowo.baseserver.demo;

import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.baseserver.base.BaseConfig;
import com.itgowo.baseserver.base.Dispatcher;

public class DemoMain {
    private static HttpServerManager mHttpServer = new HttpServerManager();
    private static Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {
        initServer();
    }

    private static void initServer() {
        int portint = BaseConfig.getServerPort();
        mHttpServer.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());

        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
        if (BaseConfig.isAnalysisTps())dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.actionScanner(DemoMain.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        mHttpServer.setOnServerListener(dispatcher);

        int finalPortint = portint;
        Thread mGameThread = new Thread(() -> {
            try {
                Thread.currentThread().setName("GameMainThread");
                mHttpServer.start(finalPortint);
            } catch (Exception mEm) {
                mEm.printStackTrace();
            }
        });
        mGameThread.start();

    }
}
