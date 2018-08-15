package com.itgowo.baseServer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ServerClassLoader extends ClassLoader {
    private String packageName;

    public ServerClassLoader(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected Class<?> findClass(String fileName) throws ClassNotFoundException {
        File file = new File(fileName);
        if (!fileName.endsWith(".class")) {
            fileName = fileName + ".class";
        }
        try {
            try (FileInputStream in = new FileInputStream(fileName)) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    byte[] data = out.toByteArray();
                    if (!packageName.endsWith(".")) {
                        packageName = packageName + ".";
                    }
                    packageName=packageName.replace("/",".");
                    packageName=packageName.replace("\\",".");
                    String name=file.getName().substring(0,file.getName().length()-6);
                    return defineClass(packageName + name, data, 0, data.length);
                }
            }
        } catch (IOException e) {
            throw new ClassNotFoundException(fileName);
        }
    }


}
