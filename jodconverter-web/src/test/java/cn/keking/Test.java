package cn.keking;

import java.util.Properties;

/**
 * @author li jiafang
 * @create 2019/11/13 18:09
 * @describe:
 */

public class Test {


    public static void main(String[] args) {
        boolean b = isOSLinux();
        System.out.println(b);

    }

    private static boolean isOSLinux() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("linux") > -1) {
            return true;
        }else {
            return false;
        }

    }
}
