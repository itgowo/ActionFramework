package com.itgowo.mybatisframework;

import com.itgowo.BaseConfig;
import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.utils.Utils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MybatisManager {

    private static SqlSessionFactory mSqlSessionFactory;
    private static SqlSessionFactoryBuilder mSqlSessionFactoryBuilder;
    private static AtomicBoolean isReload = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock();

    public static SqlSessionFactory getSqlSessionFactory() {
        if (mSqlSessionFactoryBuilder == null) {
            mSqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        }
        if (mSqlSessionFactory == null) {
            Configuration configuration = new Configuration(new Environment("MybatisManager", new JdbcTransactionFactory(), DataSourceFactory.getDataSource()));
            configuration.setLazyLoadingEnabled(true);
            String s = BaseConfig.getConfigServerMybatisLogimplClass();
            Class logClass = null;
            try {
                logClass = Class.forName(s);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (logClass != null) {
                configuration.setLogImpl(Log4j2Impl.class);
            }
            mSqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            loadMapper();
        }
        if (isReload.get()) {
            lock.lock();
            lock.unlock();
        }
        return mSqlSessionFactory;
    }

    public static boolean reloadMapper() {
        lock.lock();
        isReload.set(true);
        ServerManager.getLogger().info("重新加载Mapper");
        try {
            removeConfig(mSqlSessionFactory.getConfiguration());
            loadMapper();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            isReload.set(false);
            lock.unlock();
        }
    }

    public static <T> T getDao(Class<T> c) {
        return getSqlSessionFactory().openSession(true).getMapper(c);
    }

    /**
     * 加载开发时运行路径和jar文件路径内的xml文件，默认排除目录META-INF
     */
    private static void loadMapper() {
        try {

            Class c = null;
            try {
                c = Class.forName(BaseConfig.getServerMainClass());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (c == null) {
                c = MybatisManager.class;
            }
            File file = new File(c.getProtectionDomain().getCodeSource().getLocation().getFile());
            List<File> files = new ArrayList<>();
            File df = new File(BaseConfig.getConfigServerMybatisMapperDynamicPath());
            if (df != null && df.exists() && df.isDirectory()) {
                files.addAll(Utils.getAllFileFromDir(df, BaseConfig.getConfigServerMybatisMapperPath(), ".xml"));
            }
            boolean isJar = false;
            if (file.isDirectory()) {
                file = file.getParentFile();
                files.addAll(Utils.getAllFileFromDir(file, BaseConfig.getConfigServerMybatisMapperPath(), ".xml"));
            } else {
                isJar = true;
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                String per = BaseConfig.getConfigServerMybatisMapperPath();
                while (entryEnumeration.hasMoreElements()) {
                    JarEntry entry = entryEnumeration.nextElement();
                    if (entry.getName().endsWith(".xml") && !entry.getName().startsWith("META-INF") && (per == null ? true : entry.getName().startsWith(per))) {
                        files.add(new File(entry.getName()));
                    }
                }
            }

            for (int i = 0; i < files.size(); i++) {
                try {
                    InputStream inputStream = null;
                    if (isJar) {
                        inputStream = Resources.getResourceAsStream(files.get(i).toString());
                    } else {
                        inputStream = new FileInputStream(files.get(i));
                    }
                    XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, mSqlSessionFactory.getConfiguration(), "", mSqlSessionFactory.getConfiguration().getSqlFragments());
                    builder.parse();
                    ServerManager.getLogger().info("Mapper添加成功：" + files.get(i));
                } catch (Exception e) {
                    ServerManager.getLogger().error(e);
                    ServerManager.getLogger().info("Mapper解析失败：" + files.get(i));
                }
            }
            ServerManager.getLogger().info("加载Mapper完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空Configuration中几个重要的缓存
     *
     * @param configuration
     * @throws Exception
     */
    private static void removeConfig(Configuration configuration) throws Exception {
        Class<?> classConfig = configuration.getClass();
        clearMap(classConfig, configuration, "mappedStatements");
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");
        clearSet(classConfig, configuration, "loadedResources");
    }

    @SuppressWarnings("rawtypes")
    private static void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map mapConfig = (Map) field.get(configuration);
        mapConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private static void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set setConfig = (Set) field.get(configuration);
        setConfig.clear();
    }

    public static class DataSourceFactory {
        public static DataSource getDataSource() {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = BaseConfig.getServerMySQLUrl();
            String username = BaseConfig.getServerMySQLUser();
            String password = BaseConfig.getServerMySQLPassword();
            PooledDataSource dataSource = new PooledDataSource(driver, url, username, password);
            return dataSource;
        }
    }
}
