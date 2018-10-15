package com.itgowo.servercore.httpclient;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

public class RequestClientSync implements Callable<Response> {
    private URL url;
    private String reqestStr;
    private String requestMethod = "POST";
    private int timeout = 15000;

    public RequestClientSync(String url, String method, int timeout, String reqestStr) throws MalformedURLException {
        this.reqestStr = reqestStr;
        this.requestMethod = method;
        this.timeout = timeout;
        if (url != null) {
            if (!url.startsWith("http://") & !url.startsWith("https://")) {
                url = "http://" + url;
            }
        }
        this.url = new URL(url);

    }

    @Override
    public Response call() throws Exception {
        HttpURLConnection httpConn = null;
        httpConn = (HttpURLConnection) url.openConnection();
        Response response = new Response();
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = httpConn.getInputStream();
        byte[] bytes = new byte[1024];
        int count;
        while ((count = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, count);
        }
        if (HttpURLConnection.HTTP_OK == resultCode) {
            return response.setBody(outputStream.toByteArray()).setRequest(reqestStr).setMethod(requestMethod);
        }
        throw new Exception("http code:" + resultCode + " http message:" + outputStream.toString());
    }
}
