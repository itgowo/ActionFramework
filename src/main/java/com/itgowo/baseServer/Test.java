package com.itgowo.baseServer;

import com.itgowo.baseServer.utils.ServerClassLoader;
import com.itgowo.baseServer.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
//       List list= Utils.getClassByDir("/Users/lujianchao/111/GetHeroListWithAttr.class",true,"/Users/lujianchao/111","com.game.stzb.action");
//        System.out.println(list);
//        ClassLoader classLoader = null;
//        File file=new File("/Users/lujianchao/111/GetHeroListWithAttr.class");
//        try {
//            classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()},ClassLoader.getSystemClassLoader());
//          Class c=  classLoader.loadClass("com.game.stzb.action.GetHeroListWithAttr");
//            System.out.println(c);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
       List<Class> classes= Utils.getClassByDir(new File("/Users/lujianchao/111"),"com.game.stzb.action");
        System.out.println(classes);

    }
}
