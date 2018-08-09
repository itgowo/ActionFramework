package com.itgowo.baseServer.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class BaseConfig {
    private static Properties configProperties;
    public static final String CONFIG_SERVER_PORT = "ServerPort";
    public static final String CONFIG_SERVER_REDIS_URL = "ServerRedisUrl";
    public static final String CONFIG_SERVER_REDIS_AUTH = "ServerRedisAuth";
    public static final String CONFIG_SERVER_MYSQL_URL = "ServerMySQLUrl";
    public static final String CONFIG_SERVER_MYSQL_USER = "ServerMySQLUser";
    public static final String CONFIG_SERVER_MYSQL_PASSWORD = "ServerMySQLPassword";
    public static final String CONFIG_SERVER_THREAD_CORE_NUM = "ServerThreadCoreNum";
    public static final String CONFIG_SERVER_THREAD_MAX_NUM = "ServerThreadMaxNum";
    public static final String CONFIG_SERVER_THREAD_CACHE_TIME = "ServerThreadCacheTime";
    public static final String CONFIG_SERVER_NETTY_BOSSGROUP_THREAD_NUM = "ServerNettyBossGroupThreadNum";
    public static final String CONFIG_SERVER_NETTY_WORKER_THREAD_NUM = "ServerNettyWorkerThreadNum";
    private static Integer serverPort = null;
    private static Integer threadCoreNum = null;
    private static Integer threadMaxNum = null;
    private static Integer threadCacheTime = null;
    private static Integer nettyBossGroupThreadNum = null;
    private static Integer nettyWorkerGroupThreadNum = null;
    private static String serverMySQLUrl = null;
    private static String serverRedisUrl = null;

    public static String getProperty(String propertyName, String defaultValue) {
        try {
            defaultValue = new String(defaultValue.getBytes(Charset.forName("utf-8")));
            File file = new File(getFilePath().getParentFile(), "config.properties");
            if (!file.exists()) {
                file.createNewFile();
            }
            if (configProperties == null) {
                configProperties = new Properties();
            }
            configProperties.load(new FileInputStream(file));
            String config = configProperties.getProperty(propertyName);
            if (config == null) {
                configProperties.setProperty(propertyName, defaultValue);
                configProperties.store(new FileOutputStream(file), "Server Config");
            } else {
                return config;
            }
            return defaultValue;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static File getFilePath() {
        String f = BaseConfig.class.getProtectionDomain().getCodeSource().getLocation().getHost();
        return new File(f);
    }

    public static int getProperty(String propertyName, int defaultValue) {
        try {
            String result = getProperty(propertyName, String.valueOf(defaultValue));
            return Integer.valueOf(result);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getServerPort() {
        if (serverPort == null) {
            serverPort = getProperty(CONFIG_SERVER_PORT, 1661);
        }
        return serverPort;
    }

    public static String getServerMySQLUrl() {
        if (serverMySQLUrl == null) {
            serverMySQLUrl = getProperty(CONFIG_SERVER_MYSQL_URL, "jdbc:mysql://localhost:3306/dbname?characterEncoding=utf-8&amp;useSSL=false");
        }
        return serverMySQLUrl;
    }

    public static String getServerMySQLUser() {
        return getProperty(CONFIG_SERVER_MYSQL_USER, "数据库用户名");
    }

    public static String getServerMySQLPassword() {
        return getProperty(CONFIG_SERVER_MYSQL_PASSWORD, "数据库密码");
    }

    public static String getServerRedisUrl() {
        if (serverRedisUrl == null) {
            serverRedisUrl = getProperty(CONFIG_SERVER_REDIS_URL, "localhost");
        }
        return serverRedisUrl;
    }

    public static String getServerRedisAuth() {
        return getProperty(CONFIG_SERVER_REDIS_AUTH, "redis密码");
    }

    public static Integer getThreadCoreNum() {
        if (threadCoreNum == null) {
            threadCoreNum = getProperty(CONFIG_SERVER_THREAD_CORE_NUM, 10);
        }
        return threadCoreNum;
    }

    public static Integer getThreadMaxNum() {
        if (threadMaxNum == null) {
            threadMaxNum = getProperty(CONFIG_SERVER_THREAD_MAX_NUM, 200);
        }
        return threadMaxNum;
    }

    public static Integer getThreadCacheTime() {
        if (threadCacheTime == null) {
            threadCacheTime = getProperty(CONFIG_SERVER_THREAD_CACHE_TIME, 180);
        }
        return threadCacheTime;
    }

    public static Integer getNettyBossGroupThreadNum() {
        if (nettyBossGroupThreadNum == null) {
            nettyBossGroupThreadNum = getProperty(CONFIG_SERVER_NETTY_BOSSGROUP_THREAD_NUM, 3);
        }
        return nettyBossGroupThreadNum;
    }

    public static Integer getNettyWorkerGroupThreadNum() {
        if (nettyWorkerGroupThreadNum == null) {
            nettyWorkerGroupThreadNum = getProperty(CONFIG_SERVER_NETTY_WORKER_THREAD_NUM, 6);
        }
        return nettyWorkerGroupThreadNum;
    }
}
