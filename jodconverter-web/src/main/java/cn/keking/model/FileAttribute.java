package cn.keking.model;

import java.io.Serializable;

/**
 * Created by kl on 2018/1/17.
 * Content :
 */
public class FileAttribute implements Serializable {

    private FileType type;  // 文件类型

    private String suffix;   // 文件后缀

    private String name;     // 文件名字

    private String url;

    private String decodedUrl;

    private String fileMD5;  // 上传文件的md5值

    private String filePath;  // 上传后文件的路径

    private String watermarkType; // 水印类型

    private String watermarkText; // 水印文本

    private String watermarkImagepath; // 水印图片路径

    public String getWatermarkType() {
        return watermarkType;
    }

    public void setWatermarkType(String watermarkType) {
        this.watermarkType = watermarkType;
    }

    public String getWatermarkImagepath() {
        return watermarkImagepath;
    }

    public void setWatermarkImagepath(String watermarkImagepath) {
        this.watermarkImagepath = watermarkImagepath;
    }

    public String getWatermarkText() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileAttribute() {
    }

    public FileAttribute(FileType type, String suffix, String name, String url, String decodedUrl) {
        this.type = type;
        this.suffix = suffix;
        this.name = name;
        this.url = url;
        this.decodedUrl = decodedUrl;
    }

    @Override
    public String toString() {
        return "FileAttribute{" +
                "type=" + type +
                ", suffix='" + suffix + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", decodedUrl='" + decodedUrl + '\'' +
                ", fileMD5='" + fileMD5 + '\'' +
                ", filePath='" + filePath + '\'' +
                ", watermarkType='" + watermarkType + '\'' +
                ", watermarkText='" + watermarkText + '\'' +
                ", watermarkImagepath='" + watermarkImagepath + '\'' +
                '}';
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }


    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDecodedUrl() {
        return decodedUrl;
    }

    public void setDecodedUrl(String decodedUrl) {
        this.decodedUrl = decodedUrl;
    }
}
