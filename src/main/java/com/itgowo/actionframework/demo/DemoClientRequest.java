package com.itgowo.actionframework.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itgowo.actionframework.base.BaseRequest;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DemoClientRequest extends BaseRequest {
    private String flag = "GameSTZB";
    private String token;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer serverVersion;
    private Integer appVersion;
    private String data;
    private String sign;

    public Integer getServerVersion() {
        return serverVersion;
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
        basestring.append("ABCDTRjEd8c66HpizDYGOXzdL9003LPEnfZsh6BZe5sr3NZTjFDKer64HUvamXtP");
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
//	        throw new IOException(ex);
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

    public boolean validParameter() {
        if (action == null || action.trim().length() < 1) {
            return false;
        }
//        if (token == null || token.trim().length() < 16) {
//            return false;
//        }
//        if (flag == null || flag.trim().length() < 1) {
//            return false;
//        }
        return true;
    }

    public boolean validSign() {
//        if (sign == null || sign.trim().length() < 16) {
//            return false;
//        }
//        return sign.equals(fixSign());
        return true;
    }

    public boolean validTimeDifference(long l) {
        return System.currentTimeMillis() - timeStamp > l;
    }

    public String getSign() {
        return sign;
    }

    public DemoClientRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public DemoClientRequest setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public DemoClientRequest setServerVersion(Integer serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public int getAppVersion() {
        return appVersion == null ? 0 : appVersion;
    }

    public DemoClientRequest setAppVersion(Integer appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public DemoClientRequest setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getToken() {
        return token;
    }

    public DemoClientRequest setToken(String token) {
        this.token = token;
        return this;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public DemoClientRequest setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public DemoClientRequest setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getData() {
        return data;
    }


    public DemoClientRequest setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public <T> T getData(Class<T> aClass) throws Exception {
        return JSON.parseObject(data, aClass);
    }

    @Override
    public String initToken() {
        return null;
    }


    public static class DataEntity {
        private Integer randomNum;
        private Integer id;

        public Integer getRandomNum() {
            return randomNum;
        }

        public DataEntity setRandomNum(Integer mRandomNum) {
            randomNum = mRandomNum;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public DataEntity setId(Integer id) {
            this.id = id;
            return this;
        }
    }

}
