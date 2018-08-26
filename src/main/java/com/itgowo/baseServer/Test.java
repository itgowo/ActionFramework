package com.itgowo.baseServer;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.SimpleServerCore.Http.HttpServerManager;
import com.itgowo.SimpleServerCore.Utils.LogU;
import com.itgowo.baseServer.View.ConfigWindow;
import com.itgowo.baseServer.base.ActionRequest;
import com.itgowo.baseServer.base.BaseConfig;
import com.itgowo.baseServer.base.BaseRequest;
import com.itgowo.baseServer.base.Dispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    private static HttpServerManager mHttpServer = new HttpServerManager();
    private static Logger log = LogU.getLogU("com.itgowo.server.HttpServerManager", Level.ALL);
    private static Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {
//        initServer();
        ConfigWindow.showConfigWindow();
    }

    private static void initServer() {
        int portint = BaseConfig.getServerPort();
        mHttpServer.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());

        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
        dispatcher.startWatchAction();
        dispatcher.actionScanner(Test.class);
        dispatcher.setDispatcherListener(new Dispatcher.onDispatcherListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void doRequestOtherMethod(HttpServerHandler handler, ActionRequest actionRequest) {

            }

            @Override
            public boolean interrupt(HttpServerHandler handler) {
                return false;
            }

            @Override
            public BaseRequest parseJson(String string) throws Exception {
//                return JSON.parseObject(string,ClientRequest.class);
                return null;
            }

            @Override
            public String toJson(Object o) throws Exception {
//                return JSON.toJSONString(o);
                return null;
            }
        });
        mHttpServer.setOnReceiveHandleListener(dispatcher);

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
