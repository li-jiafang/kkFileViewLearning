package cn.keking.utils;

import java.util.Properties;

/**
 * @author li jiafang
 * @create 2019/11/13 18:13
 * @describe:
 * 判断系统类型
 * 如果系统是win 返回false
 * 如果系统是linux  返回true
 */
public class SystemTypeUtils {
    public static boolean isOSLinux() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("linux") > -1) {
            return true;
        }else {
            return false;
        }
    }
}
