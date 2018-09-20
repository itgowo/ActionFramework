package com.itgowo.actionframework.classutils;

public class ClassEntry {
    private Class classObject;
    private String filePath;
    private String packageString;
    private String className;
    private String classSimpleName;
    private String rootPath;

    public ClassEntry(Class aClass, String filePath) {
        this.classObject = aClass;
        this.filePath = filePath;
    }

    public ClassEntry() {
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getClassSimpleName() {
        return classSimpleName;
    }

    public ClassEntry setClassSimpleName(String classSimpleName) {
        this.classSimpleName = classSimpleName;
        return this;
    }

    public ClassEntry setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String getPackageString() {
        return packageString;
    }

    public ClassEntry setPackageString(String packageString) {
        this.packageString = packageString;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ClassEntry setClassName(String className) {
        this.className = className;
        return this;
    }

    public Class getClassObject() {
        return classObject;
    }

    public ClassEntry setClassObject(Class classObject) {
        this.classObject = classObject;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public ClassEntry setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ClassEntry parseClassInfo(String className) {
        if (className.endsWith("class")) {
            this.className = className.replace("/", ".").replace("\\", ".");
            this.className = this.className.substring(0, this.className.length() - 6);
        }
        int index = this.className.lastIndexOf(".");
        this.packageString = this.className.substring(0, index);
        this.classSimpleName = this.className.substring(index + 1, this.className.length());
        return this;
    }

    public ClassEntry parseClassInfo(String rootPath, String filePath) {
        this.rootPath = rootPath.trim();
        this.filePath = filePath.trim();
        int position = this.filePath.trim().indexOf(rootPath);
        if (position >= 0) {
            this.className = this.filePath.substring(position + this.rootPath.length() + 1).replace("/", ".").replace("\\", ".");
            this.className = this.className.substring(0, this.className.length() - 6);
        }
        int index = this.className.lastIndexOf(".");
        this.packageString = this.className.substring(0, index);
        this.classSimpleName = this.className.substring(index + 1, this.className.length());
        return this;
    }

    @Override
    public String toString() {
        return "ClassEntry{" +
                "classObject=" + classObject +
                ", filePath='" + filePath + '\'' +
                ", packageString='" + packageString + '\'' +
                ", className='" + className + '\'' +
                ", classSimpleName='" + classSimpleName + '\'' +
                ", rootPath='" + rootPath + '\'' +
                '}';
    }
}
