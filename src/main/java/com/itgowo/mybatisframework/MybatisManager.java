package com.itgowo.mybatisframework;

import com.itgowo.BaseConfig;
import com.itgowo.actionframework.utils.Utils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MybatisManager {

    private static SqlSessionFactory mSqlSessionFactory;
    private static SqlSessionFactoryBuilder mSqlSessionFactoryBuilder;

    public static SqlSessionFactory getSqlSessionFactory() {
        if (mSqlSessionFactoryBuilder == null) {
            mSqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        }
        if (mSqlSessionFactory == null) {
            Configuration configuration = new Configuration(new Environment("MybatisManager", new JdbcTransactionFactory(), DataSourceFactory.getDataSource()));
            configuration.setLazyLoadingEnabled(true);
            mSqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            try {
                File file = new File(MybatisManager.class.getProtectionDomain().getCodeSource().getLocation().getFile());

                List<File> files = new ArrayList<>();
                boolean isJar = false;
                if (file.isDirectory()) {
                    file = file.getParentFile();
                    files = Utils.getAllFileFromDir(file, ".xml");
                } else {
                    isJar = true;
                    JarFile jarFile = new JarFile(file);
                    Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                    while (entryEnumeration.hasMoreElements()) {
                        JarEntry entry = entryEnumeration.nextElement();

                        if (entry.getName().endsWith(".xml") && !entry.getName().startsWith("META-INF")) {
                            files.add(new File(entry.getName()));
                        }
                    }
                }
                for (int i = 0; i < files.size(); i++) {
                    InputStream inputStream = null;
                    System.out.println(files.get(i));
                    try {
                        if (isJar) {
                            System.out.println(isJar);
                            inputStream = Resources.getResourceAsStream(files.get(i).toString());
                        } else {
                            inputStream = new FileInputStream(files.get(i));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, mSqlSessionFactory.getConfiguration(), "", mSqlSessionFactory.getConfiguration().getSqlFragments());
                    builder.parse();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mSqlSessionFactory;
    }

    public static class DataSourceFactory {
        public static DataSource getDataSource() {
            String driver = "com.mysql.jdbc.Driver";
            String url = BaseConfig.getServerMySQLUrl();
            String username = BaseConfig.getServerMySQLUser();
            String password = BaseConfig.getServerMySQLPassword();
            PooledDataSource dataSource = new PooledDataSource(driver, url, username, password);
            return dataSource;
        }
    }
}
