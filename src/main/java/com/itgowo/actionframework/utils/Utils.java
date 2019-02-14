package com.itgowo.actionframework.utils;

import com.itgowo.actionframework.classutils.ClassEntry;
import com.itgowo.actionframework.classutils.ServerClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
    /**
     * 从jar文件中读取指定目录下面的所有的class文件
     *
     * @param jarPaht     jar文件存放的位置
     * @param packageName 指定的文件目录
     * @return 所有的的class的对象
     */
    public static List<ClassEntry> getClasssFromJarFile(String jarPaht, String packageName) {
        List<ClassEntry> clazzs = new ArrayList<>();

        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = (JarEntry) ee.nextElement();
            // 过滤我们出满足我们需求的东西
            if (entry.getName().startsWith(packageName) && entry.getName().endsWith(".class")) {
                jarEntryList.add(entry);
            }
        }
        for (JarEntry entry : jarEntryList) {
            String className = entry.getName().replace('/', '.');
            className = className.substring(0, className.length() - 6);
            try {
                Class c = Thread.currentThread().getContextClassLoader().loadClass(className);
                clazzs.add(new ClassEntry(c, entry.getName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazzs;
    }

    /**
     * 从IDE工作目录内加载class文件
     *
     * @param filePath
     * @param childPackage
     * @param oriFilePath
     * @param packageName
     * @return
     */
    public static List<ClassEntry> getClassNameByFile(String filePath, boolean childPackage, String oriFilePath, String packageName) {
        if (filePath == null || filePath.trim().length() == 0) {
            return new ArrayList();
        }
        List<ClassEntry> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage, oriFilePath, packageName));
                }
            } else {
                String childFilePath = childFile.getAbsolutePath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.replace(oriFilePath, "");
                    childFilePath = childFilePath.replace(".class", "");
                    if (childFilePath.startsWith("/")) {
                        childFilePath = childFilePath.substring(1, childFilePath.length());
                    }
                    if (childFilePath.startsWith(packageName)) {
                        childFilePath = childFilePath.replace("/", ".");
                        try {
                            Class c = Class.forName(childFilePath);
                            myClassName.add(new ClassEntry(c, childFile.getAbsolutePath()));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return myClassName;
    }

    public static List<ClassEntry> getClassByDir(File rootDir, String packageName) {
        List<ClassEntry> classes = new ArrayList<>();
        if (rootDir == null || !rootDir.exists()) {
            return classes;
        }
        List<File> files = getAllFileFromDir(rootDir, "class");
        ServerClassLoader serverClassLoader = new ServerClassLoader(packageName);
        for (File file : files) {
            try {
                Class c = serverClassLoader.loadClass(file.getAbsolutePath());
                classes.add(new ClassEntry(c, file.getAbsolutePath()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    public static ClassEntry getClassByClassFile(File classFile, String packageName) {
        if (!classFile.getName().toLowerCase().endsWith(".class")) {
            return null;
        }
        Class classe = null;
        ServerClassLoader serverClassLoader = new ServerClassLoader(packageName);
        try {
            classe = serverClassLoader.loadClass(classFile.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ClassEntry(classe, classFile.getAbsolutePath());
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


    public static void clossClass(Class c) {
        try {
            ClassLoader classLoader = c.getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader loader = (URLClassLoader) c.getClassLoader();
                loader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过常量属性名字,在字节码中获取到一个常量的值
     * 这个方法常用于取出接口中常量的值,可以适用于接口中的常量;类中的常量,但是仅限于常量.如果是变量,返回为空
     * 注意: 如果你要取出来的不是常量,我也给你返回了null,这是为了确保,取出来的一定是 final 的常量
     *
     * @param fieldName 属性的名字
     * @param object    在那个字节码中
     * @throws Exception 反射的风险异常.
     */
    public static Object getFieldValueByName(String fieldName, Object object) throws Exception {
        try {
            Field field = object.getClass().getField(fieldName);
            if (field == null) {
                field = object.getClass().getSuperclass().getField(fieldName);
            }
            //设置对象的访问权限，保证对private的属性的访问
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}
