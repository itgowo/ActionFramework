package com.itgowo.actionframework.base;

import com.itgowo.actionframework.ServerManager;

/**
 * Created by lujianchao on 2017/4/5.
 * 服务返回Json的基层结构
 */
public class ServerJsonEntity {
    public static final int Success = 1;
    public static final int Fail = 2;
    public static final int other = 3;
    public static final int Code404 = 404;
    public static final String Code_Success = "success";
    public static final String Code_Fail = "fail";
    private int code = 1;
    private String msg = Code_Success;
    private String error = null;
    private Object data = null;


    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public ServerJsonEntity setError(String error) {
        this.error = error;
        return this;
    }

    public ServerJsonEntity setCode(int mCode) {
        code = mCode;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ServerJsonEntity setMsg(String mMsg) {
        msg = mMsg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ServerJsonEntity setData(Object mData) {
        data = mData;
        return this;
    }

    @Override
    public String toString() {
        try {
            return toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public String toJson() throws Exception {
        return ServerManager.getOnJsonConvertListener().toJson(this);
    }
}
