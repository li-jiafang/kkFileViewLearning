package cn.keking.utils;


import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author li jiafang
 * @create 2019/11/15 17:27
 * @describe: 计算文件的md5值，并返回string类型,用于判断文件是否重复
 */

public class FileMD5StringUtil {
    public static String getFileMD5String(MultipartFile file){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inputStream.read(buffer,0,1024)) > 0){
                messageDigest.update(buffer,0,length);

            }
            inputStream.close();
            return new BigInteger(1,messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
