package com.itgowo.servercore.httpclient;

import java.util.concurrent.*;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpClient {
    private static final String TAG = "itgowo-HttpClient";
    private static int timeout = 15000;
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 15, 60 * 1000 * 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(TAG);
            return thread;
        }
    });

    public static void setTimeout(int timeout1) {
        timeout = timeout1;
    }

    public static void RequestGet(String url, String requestJson, onCallbackListener listener) {
        Request(url, HttpMethod.GET, requestJson, listener);
    }

    public static void RequestPOST(String url, String requestJson, onCallbackListener listener) {
        Request(url, HttpMethod.POST, requestJson, listener);
    }

    public static void Request(String url, HttpMethod method, String requestJson, onCallbackListener listener) {
        executorService.execute(new RequestClient(url, method.getMethod(), requestJson, timeout, listener));
    }

    public static Response RequestSync(String url, HttpMethod method, String requestJson) throws Exception {
        FutureTask futureTask = (FutureTask) executorService.submit(new RequestClientSync(url, method.getMethod(), timeout, requestJson));
        return (Response) futureTask.get();
    }

}
