package com.itgowo.baseServer.view1;

public class SystemInfoItemBean {
    private String key;
    private String value;

    public SystemInfoItemBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public SystemInfoItemBean setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public SystemInfoItemBean setValue(String value) {
        this.value = value;
        return this;
    }
}
