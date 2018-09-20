package com.itgowo.actionframework.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.onJsonConvertListener;
import com.itgowo.servercore.http.HttpServerManager;
import com.itgowo.actionframework.base.BaseConfig;
import com.itgowo.actionframework.base.Dispatcher;
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
        dispatcher.setJsonConvertListener(new onJsonConvertListener<DemoClientRequest>() {
            @Override
            public DemoClientRequest parseJson(String string) throws Exception {
                return JSON.parseObject(string,DemoClientRequest.class);
            }

            @Override
            public String toJson(Object o) throws Exception {
                return JSON.toJSONString(o);
            }
        });
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
