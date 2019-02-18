package com.itgowo.actionframework.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.BaseConfig;
import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.Dispatcher;
import com.itgowo.actionframework.base.onJsonConvertListener;
import com.itgowo.servercore.http.HttpServerManager;

public class DemoMain {
    private static HttpServerManager httpServerManager = new HttpServerManager("","DemoServer");
    private static Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {
        initServer();
    }

    private static void initServer() {
        int portint = BaseConfig.getServerPort();
        ServerManager.setOnJsonConvertListener(new onJsonConvertListener<DemoClientRequest>() {
            @Override
            public DemoClientRequest parseJson(String string) throws Exception {
                return JSON.parseObject(string, DemoClientRequest.class);
            }

            @Override
            public <T> T parseJson(String string, Class<T> tClass) throws Exception {
                return JSON.parseObject(string, tClass);
            }

            @Override
            public String toJson(Object o) throws Exception {
                return JSON.toJSONString(o);
            }
        });
        httpServerManager.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
//        if (BaseConfig.isAnalysisTps())dispatcher.startAnalysisTps();
//        dispatcher.startWatchAction();
        dispatcher.setRootPath("demo", httpServerManager);
        dispatcher.actionScanner(DemoMain.class);
        dispatcher.setDispatcherListener(new DemoDispatcher());
        httpServerManager.setOnServerListener(dispatcher);

        int finalPortint = portint;
        Thread mGameThread = new Thread(() -> {
            try {
                Thread.currentThread().setName("GameMainThread");
                httpServerManager.prepare(finalPortint);
                httpServerManager.start();
            } catch (Exception mEm) {
                mEm.printStackTrace();
            }
        });
        mGameThread.start();

    }
}
