package com.itgowo.servercore.httpclient;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class Response {
    private boolean isSuccess = true;
    private byte[] body;
    private String request;
    private String method;

    public boolean isSuccess() {
        return isSuccess;
    }

    public Response setSuccess(boolean success) {
        isSuccess = success;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodaStr() {
        return body == null ? "" : new String(body);
    }

    public Response setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public String getRequest() {
        return request;
    }

    public Response setRequest(String request) {
        this.request = request;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Response setMethod(String method) {
        this.method = method;
        return this;
    }
}