package com.itgowo.baseServer.utils;

public class ClassEntry {
    private Class aClass;
    private String filePath;

    public ClassEntry(Class aClass, String filePath) {
        this.aClass = aClass;
        this.filePath = filePath;
    }

    public Class getaClass() {
        return aClass;
    }

    public ClassEntry setaClass(Class aClass) {
        this.aClass = aClass;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public ClassEntry setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
}
