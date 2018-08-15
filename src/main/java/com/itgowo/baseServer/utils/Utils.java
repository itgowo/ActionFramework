package com.itgowo.baseServer.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
    public static List<Class> getClasssFromJarFile(String jarPaht, String packageName) {
        List<Class> clazzs = new ArrayList<Class>();

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
                clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(className));
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
    public static List<Class> getClassNameByFile(String filePath, boolean childPackage, String oriFilePath, String packageName) {
        if (filePath == null || filePath.trim().length() == 0) {
            return new ArrayList();
        }
        List<Class> myClassName = new ArrayList<>();
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
                            myClassName.add(Class.forName(childFilePath));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return myClassName;
    }

    public static List<Class> getClassByDir(File rootDir, String packageName) {
        List<Class> classes = new ArrayList<>();
        if (rootDir == null || !rootDir.exists()) {
            return classes;
        }
        List<File> files = getAllFileFromDir(rootDir, "class");
        ServerClassLoader serverClassLoader = new ServerClassLoader(packageName);
        for (int i = 0; i < files.size(); i++) {
            try {
                classes.add(serverClassLoader.loadClass(files.get(i).getAbsolutePath()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
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

    /**
     * 通过常量属性名字,在字节码中获取到一个常量的值
     * 这个方法常用于取出接口中常量的值,可以适用于接口中的常量;类中的常量,但是仅限于常量.如果是变量,返回为空
     * 注意: 如果你要取出来的不是常量,我也给你返回了null,这是为了确保,取出来的一定是 final 的常量
     *
     * @param fieldName 属性的名字
     * @param clazz     在那个字节码中
     * @return 如果找找到, 并且他也是常量, 我就返回它的值, 否则返回null
     * @throws Exception 反射的风险异常.
     */
    public static Object getFinalFieldValueByName(String fieldName, Class<?> clazz) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //确保你要取的,是常量.否则,就不用进去这个获取操作了
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            if (isFinal) {
                String name = field.getName();
                if (fieldName.equals(name)) {
                    return field.get(null);
                }
            }
        }
        return null;
    }
}
