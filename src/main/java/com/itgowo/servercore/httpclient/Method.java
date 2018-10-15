package com.itgowo.servercore.httpclient;
/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class Method {
    private String method = "POST";
    private static final String[] methods = new String[]{"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
    public static final Method GET = new Method(methods[0]);
    public static final Method POST = new Method(methods[1]);
    public static final Method HEAD = new Method(methods[2]);
    public static final Method OPTIONS = new Method(methods[3]);
    public static final Method PUT = new Method(methods[4]);
    public static final Method DELETE = new Method(methods[5]);
    public static final Method TRACE = new Method(methods[6]);

    public static Method parse(String method) {
        if (methods[0].equalsIgnoreCase(method)) {
            return GET;
        }
        if (methods[1].equalsIgnoreCase(method)) {
            return POST;
        }
        if (methods[2].equalsIgnoreCase(method)) {
            return HEAD;
        }
        if (methods[3].equalsIgnoreCase(method)) {
            return OPTIONS;
        }
        if (methods[4].equalsIgnoreCase(method)) {
            return PUT;
        }
        if (methods[5].equalsIgnoreCase(method)) {
            return DELETE;
        }
        if (methods[6].equalsIgnoreCase(method)) {
            return TRACE;
        }
        return new Method(method.toUpperCase());
    }

    private Method(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}