package com.itgowo.actionserver.demo;

import com.itgowo.actionserver.ServerManager;
import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.actionserver.base.BaseConfig;
import com.itgowo.actionserver.base.Dispatcher;
import com.itgowo.servercore.utils.LogU;

import java.util.logging.Level;

public class DemoMain {
    private static HttpServerManager mHttpServer = new HttpServerManager();
    private static Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {
        initServer();
    }

    private static void initServer() {
        int portint = BaseConfig.getServerPort();
        ServerManager.setLogger(LogU.getLogU("DemoServer",Level.ALL));
        mHttpServer.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
//        if (BaseConfig.isAnalysisTps())dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.actionScanner(DemoMain.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        mHttpServer.setOnServerListener(dispatcher);
        mHttpServer.setServerName("DemoServer");
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
