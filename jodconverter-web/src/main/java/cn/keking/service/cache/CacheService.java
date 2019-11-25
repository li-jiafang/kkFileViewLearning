package cn.keking.service.cache;

import cn.keking.model.FileAttribute;

import java.util.List;
import java.util.Map;

/**
 * @auther: chenjh
 * @time: 2019/4/2 16:45
 * @description
 */
public interface CacheService {
    final String REDIS_FILE_PREVIEW_PDF_KEY = "converted-preview-pdf-file";
    final String REDIS_FILE_PREVIEW_IMGS_KEY = "converted-preview-imgs-file";//压缩包内图片文件集合
    final String REDIS_FILE_PREVIEW_PDF_IMGS_KEY = "converted-preview-pdfimgs-file";
    final String REDIS_FILE_PREVIEW_FILEATTRIBUTE_KEY = "converted-preview-fileattribute-file"; // FileAttribute对象属性集合
    final String REDIS_FILE_WATERMARK_PDF_KEY = "converted-preview-watermark-file";

    final Integer DEFAULT_PDF_CAPACITY = 500000;
    final Integer DEFAULT_IMG_CAPACITY = 500000;
    final Integer DEFAULT_PDFIMG_CAPACITY = 500000;

    final Integer DEFAULT_FILEATTRIBUTE_CAPACITY = 500000;

    void initPDFCachePool(Integer capacity);
    void initIMGCachePool(Integer capacity);
    void initPdfImagesCachePool(Integer capacity);
    void initFileAttributeCachePool(Integer capacity);

    void putPDFCache(String key, String value);
    void putImgCache(String key, List<String> value);
    // 将上传文件添加到文件属性中
    void putFileAttributeCache(String key, List<FileAttribute> fileAttributeList);
    // 获取未解析文件的缓存
    Map<String,List<FileAttribute>> getFileAttributeCache();
    // 解析文件后清理掉这个文件的缓存
    void cleanFileAttributeCache(String key);

    // 添加水印缓存
    void putWaterMarkCache(String key,String value);
    Map<String,String> getWaterMarkCache();

    Map<String, String> getPDFCache();
    String getPDFCache(String key);
    Map<String, List<String>> getImgCache();
    List<String> getImgCache(String key);
    Integer getPdfImageCache(String key);
    void putPdfImageCache(String pdfFilePath, int num);

    void cleanCache();

    void addQueueTask(String url);
    String takeQueueTask() throws InterruptedException;



}
