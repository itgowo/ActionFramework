package com.itgowo.servercore.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestClient implements Runnable {
    private URL url;
    private String reqestStr;
    private String requestMethod = "POST";
    private onCallbackListener listener;
    private int timeout = 15000;
    private Response response = new Response();

    public RequestClient(String url, String method, String reqestStr, int timeout, onCallbackListener listener) {
        this.listener = listener;
        this.reqestStr = reqestStr;
        this.requestMethod = method;
        response.setMethod(requestMethod);
        this.timeout = timeout;
        try {
            if (url != null) {
                if (!url.startsWith("http://") & !url.startsWith("https://")) {
                    url = "http://" + url;
                }
            }
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            onError(response.setSuccess(false), e);
        }
    }

    @Override
    public void run() {
        HttpURLConnection httpConn = null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            //设置参数
            httpConn.setDoOutput(true);     //需要输出
            httpConn.setDoInput(true);      //需要输入
            httpConn.setUseCaches(false);   //不允许缓存
            httpConn.setRequestMethod(requestMethod);      //设置POST方式连接
            httpConn.setReadTimeout(timeout);

            //设置请求属性
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");

            //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
            httpConn.connect();

            //建立输入流，向指向的URL传入参数
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
            bos.write(reqestStr);
            bos.flush();
            bos.close();

            //获得响应状态
            final int resultCode = httpConn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = httpConn.getInputStream();
                byte[] bytes = new byte[1024];
                int count;
                while ((count = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, count);
                }
                response.setBody(outputStream.toByteArray());
                onSuccess(response);
            } else {
                onError(response.setSuccess(false), new Exception("http code:" + resultCode));
            }
        } catch (IOException e) {
            onError(response.setSuccess(false), e);
        }
    }

    private void onSuccess(final Response response) {
        listener.onSuccess(response);
    }

    private void onError(final Response response, final Exception e) {
        listener.onError(response, e);
    }
}
