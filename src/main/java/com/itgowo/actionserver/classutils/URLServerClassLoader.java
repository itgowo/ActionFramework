package com.itgowo.actionserver.classutils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class URLServerClassLoader extends URLClassLoader {
    private static Map<String, URLServerClassLoader> map = new HashMap<>();
    private static URLServerClassLoader defaultUrlClassLoader = new URLServerClassLoader(new URL[]{});

    public static URLServerClassLoader getInstance(String jarPathOrDir) {
        if (jarPathOrDir == null || jarPathOrDir.trim().length() < 1) {
            return defaultUrlClassLoader;
        }
        jarPathOrDir = jarPathOrDir.trim();
        synchronized (map) {
            if (map.containsKey(jarPathOrDir)) {
                return map.get(jarPathOrDir);
            } else {
                try {
                    URLServerClassLoader serverClassLoader = new URLServerClassLoader(new URL[]{new File(jarPathOrDir).toURI().toURL()});
                    map.put(jarPathOrDir, serverClassLoader);
                    return serverClassLoader;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return defaultUrlClassLoader;
                }
            }
        }
    }

    public URLServerClassLoader(URL[] urls) {
        super(urls);
    }
}
