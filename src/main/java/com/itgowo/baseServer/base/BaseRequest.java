package com.itgowo.baseServer.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.itgowo.SimpleServerCore.Http.HttpServerHandler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 客户端请求Json基类，实现了校验方法
 */
public class BaseRequest {
    private String flag;
    private String action;
    private String token;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer serverVersion;
    private Integer appVersion;
    private String data;
    private String sign;
    private long timeStamp;

    public static BaseRequest parse(HttpServerHandler handler) {
        BaseRequest request = null;
        try {
            request = JSON.parseObject(handler.getBody(Charset.forName("utf-8")), BaseRequest.class, Feature.OrderedField);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    public boolean isValid(boolean isValidSign, boolean isValidTimeLimit, long timeLimit) {
        if (action == null || action.trim().length() < 1) {
            return false;
        }
        if (token == null || token.trim().length() < 16) {
            return false;
        }
        if (flag == null || flag.trim().length() < 1) {
            return false;
        }
        if (isValidSign && (sign == null || sign.trim().length() < 32)) {
            return false;
        }
        if (isValidSign && !checkSign()) {
            return false;
        }
        if (isValidTimeLimit && isTimeOut(timeLimit)) {
            return false;
        }
        return true;
    }

    public Integer getServerVersion() {
        return serverVersion;
    }

    public boolean checkSign() {
        if (sign == null || sign.trim().length() == 0) {
            return false;
        }
        return sign.equals(fixSign());
    }

    public boolean isTimeOut(long timeLimit) {
        return System.currentTimeMillis() - timeStamp > timeLimit;
    }

    private String fixSign() {
        JSONObject paramsJsonObj = (JSONObject) JSON.toJSON(this);
        Map<String, String> signParamsMap = new TreeMap<>();
        for (Map.Entry<String, Object> param : paramsJsonObj.entrySet()) {
            // 排除参数名为sign。
            if (param != null && param.getValue() != null && !"sign".equals(param.getKey())) {
                signParamsMap.put(param.getKey(), param.getValue().toString());
            }
        }
        Set<Map.Entry<String, String>> entrys = signParamsMap.entrySet();

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append("=").append(param.getValue());
        }
        basestring.append("9AGDTRjEd8c66HpizDYGOXzdL9003LPEnfZsh6BZe5sr3NZTjFDKer64HUvamXtP");
        // 使用MD5对待签名串求签
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            try {
                bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (GeneralSecurityException ex) {
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder sign1 = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign1.append("0");
            }
            sign1.append(hex);
        }
        return sign1.toString();
    }

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

    public BaseRequest setServerVersion(Integer serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public int getAppVersion() {
        return appVersion == null ? 0 : appVersion;
    }

    public BaseRequest setAppVersion(Integer appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public BaseRequest setFlag(String flag) {
        this.flag = flag;
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

    public Integer getPageIndex() {
        return pageIndex;
    }

    public BaseRequest setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public BaseRequest setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getData() {
        return data;
    }

    public <T> T getData(Class<T> mClass) {
        return JSON.parseObject(data, mClass);
    }

    public BaseRequest setData(String data) {
        this.data = data;
        return this;
    }

    public String toJson() {
        if (token == null) {
            initToken();
        }
        return JSON.toJSONString(this);
    }

    public void initToken() {

    }
}
