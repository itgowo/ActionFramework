package com.itgowo.baseServer.base;

/**
 * 客户端请求Json基类，实现了校验方法
 */
public abstract class BaseRequest {
    public String action;
    private String token;
    private String data;
    private String sign;
    public long timeStamp;

    public String getSign() {
        return sign;
    }

    public BaseRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public BaseRequest setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }


    public String getAction() {
        return action;
    }

    public BaseRequest setAction(String action) {
        this.action = action;
        return this;
    }

    public String getToken() {
        return token;
    }

    public BaseRequest setToken(String token) {
        this.token = token;
        return this;
    }

    public String getData() {
        return data;
    }



    public BaseRequest setData(String data) {
        this.data = data;
        return this;
    }

    public   String toJson() {
        if (token == null) {
            initToken();
        }
        return toJsonTool();
    }
    public abstract <T> T getData(Class<T> mClass);
    /**
     * 如果token为空，是否生成默认token，不处理返回null也可。
     *
     * @return
     */
    public abstract String initToken();

    /**
     * 实现对象转json文本
     * @return
     */
    public abstract String toJsonTool();
    /**
     * 校验其他参数
     *
     * @return
     */
    public abstract boolean validParameter();

    /**
     * 检查数据完整性，校验
     *
     * @return
     */
    public abstract boolean validSign();

    /**
     * 校验服务器与客户端时差
     *
     * @return
     */
    public abstract boolean validTimeDifference(long timeLimit);
}
