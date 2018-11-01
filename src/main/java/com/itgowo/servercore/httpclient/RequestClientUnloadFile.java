package com.itgowo.servercore.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestClientUnloadFile implements Runnable {
    private URL url;
    private String reqestStr;
    private String requestMethod = "POST";
    private onUploadFileCallbackListener listener;
    private List<String> uploadFiles;
    private int timeout = 15000;
    private HttpResponse response = new HttpResponse();
    private Map<String, String> headers = new HashMap<>();

    public RequestClientUnloadFile(String url, List<String> files, Map<String, String> headers, int timeout, onUploadFileCallbackListener listener) {
        this.listener = listener;
        this.reqestStr = reqestStr == null ? "" : reqestStr;
        response.setMethod(requestMethod);
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.timeout = timeout;
        uploadFiles = files;
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
        uploadFile(uploadFiles);
    }

    private void onSuccess(final HttpResponse response) {
        listener.onSuccess(response);
    }

    private void onError(final HttpResponse response, final Exception e) {
        listener.onError(response, e);
    }


    /**
     * 多文件上传的方法
     *
     * @param uploadFilePaths：需要上传的文件路径，数组
     * @return
     */
    @SuppressWarnings("finally")
    public HttpResponse uploadFile(List<String> uploadFilePaths) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------7a8b9c0d1e2f3g";

        DataOutputStream ds = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            HttpResponse response = new HttpResponse().setMethod(requestMethod).setRequest("");
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setReadTimeout(timeout);
            httpURLConnection.setConnectTimeout(timeout);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置是否向httpUrlConnection输出
            httpURLConnection.setDoOutput(true);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod(requestMethod);
            // 设置字符编码连接参数
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            for (Map.Entry<String, String> header : this.headers.entrySet()) {
                httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
            }
            // 设置请求内容类型
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 设置DataOutputStream
            ds = new DataOutputStream(httpURLConnection.getOutputStream());
            for (int i = 0; i < uploadFilePaths.size(); i++) {
                String uploadFile = uploadFilePaths.get(i);
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                File file = new File(filename);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + file.getName()
                        + "\" " + end);
                ds.writeBytes("Content-Type: application/octet-stream" + end);
                ds.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int count = fStream.available();
                int process = 0;
                int bufferSize = 8192;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                    process += length;
                    listener.onProcess(uploadFile, count, process);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            ds.flush();
            if (httpURLConnection.getResponseCode() >= 300) {
                response.setSuccess(false);
                onError(response, new Exception("http code:" + httpURLConnection.getResponseCode()));
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                resultBuffer = new StringBuffer();
                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
                response.setSuccess(true).setBody(resultBuffer.toString().getBytes());
                onSuccess(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            onError(response.setSuccess(false), e);
        } finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

}
