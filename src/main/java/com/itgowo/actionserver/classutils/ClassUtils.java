package com.itgowo.actionserver.classutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {
    public static List<ClassEntry> getClasssFromJarFile(String jarPaht, List<String> packageName) {
        List<ClassEntry> clazzs = new ArrayList<>();
        if (packageName != null && packageName.size() > 0) {
            for (int i = 0; i < packageName.size(); i++) {
                packageName.set(i, packageName.get(i).replace(".", "/"));
            }
        } else {
            return clazzs;
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        URLServerClassLoader classLoader = URLServerClassLoader.getInstance(jarPaht);
        List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = (JarEntry) ee.nextElement();
            // 过滤我们出满足我们需求的东西
            for (int i = 0; i < packageName.size(); i++) {
                if (packageName.get(i) == null) {
                    continue;
                }
                if (entry.getName().startsWith(packageName.get(i)) && entry.getName().endsWith(".class")) {
                    jarEntryList.add(entry);
                }
            }
        }
        for (JarEntry entry : jarEntryList) {
            ClassEntry classEntry = new ClassEntry().setRootPath(jarPaht).parseClassInfo(entry.getName()).setFilePath(jarFile.getName());
            try {
                Class c = classLoader.loadClass(classEntry.getClassName());
                classEntry.setClassObject(c);
                clazzs.add(classEntry);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazzs;
    }

    public static List<ClassEntry> getClassList(String path, List<String> filter) {
        List<ClassEntry> classEntries = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return classEntries;
        }
        if (file.isDirectory()) {
            List<File> files = getAllFileFromDir(file, "");
            for (int i = 0; i < files.size(); i++) {
                File cur = files.get(i);
                if (cur.isFile() && cur.getName().endsWith(".jar")) {
                    classEntries.addAll(getClasssFromJarFile(cur.getAbsolutePath(), filter));
                } else if (cur.getName().endsWith(".class")) {
                    ClassEntry classEntry = getClassFromFile(path, cur.getAbsolutePath(), filter);
                    if (classEntry != null) classEntries.add(classEntry);
                }
            }
        } else if (path.endsWith(".class")) {

        } else if (path.endsWith(".jar")) {
            classEntries.addAll(getClasssFromJarFile(path, filter));
        }
        return classEntries;

    }

    public static ClassEntry getClassFromFile(String root, String file, List<String> filter) {
        try {
            URLServerClassLoader classLoader = URLServerClassLoader.getInstance(root);
            ClassEntry classEntry = new ClassEntry().parseClassInfo(root, file);
            classEntry.setClassObject(classLoader.loadClass(classEntry.getClassName()));
            for (int i = 0; i < filter.size(); i++) {
                if (classEntry.getClassObject().getName().startsWith(filter.get(i))) {
                    classEntry.parseClassInfo(classEntry.getClassObject().getName());
                    return classEntry;
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取目录指定类型文件
     *
     * @param rootDir
     * @param extName
     * @return
     */
    public static List<File> getAllFileFromDir(File rootDir, String extName) {
        List<File> files = new ArrayList<>();
        if (rootDir.isFile()) {
            if (extName != null && rootDir.getAbsolutePath().endsWith(extName)) {
                files.add(rootDir);
            } else if (extName == null) {
                files.add(rootDir);
            }
        } else {
            File[] fs = rootDir.listFiles();
            for (File f : fs) {
                if (f.isDirectory())    //若是目录，则递归打印该目录下的文件
                    files.addAll(getAllFileFromDir(f, extName));
                if (f.isFile())//若是文件，直接打印
                    if (extName != null && f.getAbsolutePath().endsWith(extName)) {
                        files.add(f);
                    } else if (extName == null) {
                        files.add(f);
                    }
            }
        }
        return files;
    }

    /**
     * 获取执行文件或者执行目录
     *
     * @return
     */
    public static File getJarFile(Class c) {
        return new File(c.getProtectionDomain().getCodeSource().getLocation().getFile());
    }
}
