package com.itgowo.servercore.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RequestClientDownloadFile implements Runnable {
    private URL url;
    private String reqestStr;
    private String requestMethod = "GET";
    private onDownloadFileCallbackListener listener;
    private String downloadDir;
    private int timeout = 15000;
    private HttpResponse response = new HttpResponse();
    private Map<String, String> headers = new HashMap<>();

    public RequestClientDownloadFile(String url, String downloadDir, Map<String, String> headers, int timeout, onDownloadFileCallbackListener listener) {
        this.listener = listener;
        this.reqestStr = reqestStr == null ? "" : reqestStr;
        response.setMethod(requestMethod);
        this.timeout = timeout;
        this.downloadDir = downloadDir;
        if (headers != null) {
            this.headers.putAll(headers);
        }
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
        downloadFile(downloadDir);
    }

    private void onDownloadFileSuccess(final String downloadFile) {
        listener.onDownloadFileSuccess(downloadFile);
    }

    private void onError(final HttpResponse response, final Exception e) {
        listener.onError(response, e);
    }


    /**
     * 下载路径
     *
     * @param downloadDir 下载存放目录
     * @return 返回下载文件
     */
    public File downloadFile(String downloadDir) {
        File file = null;
        try {
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod(requestMethod);
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            for (Map.Entry<String, String> header : this.headers.entrySet()) {
                httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
            }
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();
            // 文件大小
            int fileLength = httpURLConnection.getContentLength();
            // 文件名
            String filePathUrl = httpURLConnection.getURL().getFile();
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
            BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
            String path = downloadDir + File.separatorChar + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[8192];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
                listener.onProcess(fileFullName, fileLength, len);
            }
            bin.close();
            out.close();
            onDownloadFileSuccess(path);
        } catch (IOException e) {
            e.printStackTrace();
            onError(response.setSuccess(false), e);
        } finally {
            return file;
        }

    }
}
