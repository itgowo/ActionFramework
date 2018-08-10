package com.itgowo.SimpleServerCore.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by hnvfh on 2017/3/29.
 */
public class LogU {
    public static Logger getLogU(String name, Level level) {
        Logger logger = Logger.getLogger(name );
        logger.setLevel(level);
        File file = new File( "Log");
        if (!file.exists())
            file.mkdirs();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String filename = file.getAbsolutePath() + "/" + sdf.format(new Date()) + ".log";
        FileHandler fh;
        try {
            fh = new FileHandler(filename, true);
            fh.setFormatter(new SimpleFormatter());//输出格式
            logger.addHandler(fh);//日志输出文件
//            mLogger.addHandler(new ConsoleHandler());//输出到控制台
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "安全性错误", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "读取文件日志错误", e);
        }
        return logger;
    }

}
