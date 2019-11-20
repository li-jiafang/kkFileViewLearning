//package cn.keking.config;
//
//import cn.keking.model.FileAttribute;
//import cn.keking.service.FilePreview;
//import cn.keking.service.cache.CacheService;
//import cn.keking.watermarkprocessor.WatermarkException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author li jiafang
// * @create 2019/11/20 11:01
// * @describe: 每隔一秒读取并处理一次缓存中的文件
// */
//@Component
//public class FileProcessing {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessing.class);
//
//
//    @Qualifier("cacheServiceRocksDBImpl")
//    @Autowired
//    CacheService cacheService;
//
//    @PostConstruct
//    void execute(){
//        Thread fileProcessingThread = new Thread(new FileProcessingThread());
//        fileProcessingThread.start();
//    }
//
//    class FileProcessingThread implements Runnable{
//        @Override
//        public void run() {
//            Map<String, List<FileAttribute>> fileAttributeCache = cacheService.getFileAttributeCache();
//            while (!fileAttributeCache.isEmpty()){
//                // 对map遍历来实现文件的解析
//                for (Map.Entry<String,List<FileAttribute>> entry : fileAttributeCache.entrySet()){
//                    String key = entry.getKey();
//                    FileAttribute fileAttribute = entry.getValue().get(0);
//                    // watermarkType;  水印类型 0 文字 1图片
//                    if ("0".equals(watermarkType)){
//                        fileAttribute.setWatermarkText(watermarkText);
//                    }
//                    LOGGER.info("onlinePreview-->fileAttribute 的属性:"+fileAttribute);
//                    req.setAttribute("fileKey", req.getParameter("fileKey"));
//
//                    model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
//                    LOGGER.info("onlinePreview-->model 的属性:"+model);
//
//                    FilePreview filePreview = previewFactory.get(fileAttribute);
//                    LOGGER.info("onlinePreview-->filePreview 的属性:"+filePreview);
//                    try {
//                        List<String> imgUrlsList = filePreview.filePreviewHandleList(url, model, fileAttribute,imgfile);
//                        if (imgUrlsList.size() > 0 && imgUrlsList != null){
//                            cacheService.cleanFileAttributeCache(key);
//                        }
//                    } catch (WatermarkException e) {
//                        LOGGER.error("处理水印出现异常:"+e);
//                    } catch (IOException e) {
//                        LOGGER.error("处理文件出现异常:"+e);
//                    }
//                }
//
//            }
//
//        }
//    }
//
//
//
//}
