package cn.keking.web.controller;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.service.FilePreviewFactory;
import cn.keking.service.cache.CacheService;
import cn.keking.utils.FileMD5StringUtil;
import cn.keking.utils.FileUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author li jiafang
 * @create 2019/11/15 15:28
 * @describe:
 */

@RestController
public class FileParsingController {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
    String fileDir = ConfigConstants.getFileDir();
    @Autowired
    FileUtils fileUtils;

    @Autowired
    FilePreviewFactory previewFactory;


    @Qualifier("cacheServiceRocksDBImpl")
    @Autowired
    CacheService cacheService;

    @Value("${base.url}")
    private String baseUrl;

    String demoDir = "upload";
    String demoPath = demoDir + File.separator;

    /**
     * 文件同步上传，上传后解析后返回图片地址
     * @param file  待解析文件
     * @param imgfile 上传水印图片
     * @param watermarkType  水印类型 0文本  1图片
     * @param watermarkText
     * @param model
     * @param req
     * @return
     * @throws IOException
     * @throws WatermarkException
     */
    @RequestMapping(value = "fileSynchronousUpload", method = RequestMethod.POST)
    public String fileSynchronousUpload(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "imgfile",required = false) MultipartFile imgfile,
                                              String watermarkType,String watermarkText,
                                              Model model, HttpServletRequest req) throws IOException, WatermarkException {

        // 上传文件属性赋值
        FileAttribute fileAttribute = getFileAttributeProperty(file,imgfile,watermarkType,watermarkText);

        LOGGER.info("onlinePreview-->fileAttribute 的属性:"+fileAttribute);
        req.setAttribute("fileKey", req.getParameter("fileKey"));

        model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
        LOGGER.info("onlinePreview-->model 的属性:"+model);

        FilePreview filePreview = previewFactory.get(fileAttribute);
        LOGGER.info("onlinePreview-->filePreview 的属性:"+filePreview);
        List<String> imgUrlsList = filePreview.filePreviewHandleList(fileAttribute.getUrl(), model, fileAttribute,imgfile);
        if (imgUrlsList.size() > 0 && imgUrlsList != null){
            return new ObjectMapper().writeValueAsString(new ReturnResponse<List>(200, "SUCCESS", imgUrlsList));
        }
        return new ObjectMapper().writeValueAsString(new ReturnResponse<String>(500, "fail", fileAttribute.getType()+"解析失败"));
    }

    /**
     *给FileAttribute赋值
     */
    private FileAttribute getFileAttributeProperty(MultipartFile file, MultipartFile imgfile, String watermarkType, String watermarkText) {
        // 上传文件并获取名字
        String fileName = saveAndGetFileName(file);
        /**
         * 读取新路径下的文件并开始解析文件类型
         */
        String url = baseUrl+"\\"+demoPath+fileName;
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        /**
         * 判断水印类型
         */
        if ("0".equals(watermarkType) && !watermarkText.isEmpty()){
            fileAttribute.setWatermarkText(watermarkText);
            fileAttribute.setWatermarkImagepath("");
        }

        if ("1".equals(watermarkType) && !imgfile.isEmpty()){
            // 上传图片路径
            String watermarkImageName = saveAndGetFileName(imgfile);
        }


        // 获取文件的md5值 文件内容没有改变则值不变，改变内容则值改变
        fileAttribute.setFileMD5(FileMD5StringUtil.getFileMD5String(file));
        fileAttribute.setName(fileName);
        fileAttribute.setFilePath(fileDir+demoPath+fileName);  // 获取上传文件的路径
        fileAttribute.setUrl(url);
        fileAttribute.setDecodedUrl(url);

        return fileAttribute;
    }


    /**
     * 将上传的文件存储到新的文件路径下,返回文件名字
     */

    public String saveAndGetFileName(MultipartFile file){
        LOGGER.info("fileUpload--->上传文件file名字:"+file.getOriginalFilename());
        // 获取文件名
        String fileName = file.getOriginalFilename();
        //判断是否为IE浏览器的文件名，IE浏览器下文件名会带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            fileName = fileName.substring(pos + 1);
        }
        /**
         * 将上传的文件存储到outFile，通过文件流的方式写入到这个文件夹下
         */
        File outFile = new File(fileDir + demoPath);
        LOGGER.info("fileUpload--->上传文件outFile路径:"+outFile);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }
        try(InputStream in = file.getInputStream();
            OutputStream ot = new FileOutputStream(fileDir + demoPath + fileName)){

            /**
             * 将文件写入新的地址
             */
            byte[] buffer = new byte[1024];
            int len;
            while ((-1 != (len = in.read(buffer)))) {
                ot.write(buffer, 0, len);
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    /**
     * 文件异步上传，上传后返回文件id
     * @param file
     * @param imgfile
     * @param watermarkType
     * @param watermarkText
     * @param model
     * @param req
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "fileAsynchronousUpload", method = RequestMethod.POST)
    public String fileAsynchronousUpload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("imgfile") MultipartFile imgfile,
                                         String watermarkType,String watermarkText,

                                         Model model, HttpServletRequest req) throws JsonProcessingException {
        /**
         *  获取文件名字 添加到缓存队列中解析，返回唯一状态码
         */
        String fileName = saveAndGetFileName(file);

        String fileMd5 = FileMD5StringUtil.getFileMD5String(file);
        /**
         * 读取新路径下的文件并开始解析文件类型
         */
        String url = baseUrl+"\\"+demoPath+fileName;
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        // 获取文件的md5值 文件内容没有改变则值不变，改变内容则值改变
        fileAttribute.setFileMD5(fileMd5);
        fileAttribute.setName(fileName);
        fileAttribute.setFilePath(fileDir+demoPath+fileName);  // 获取上传文件的路径
        fileAttribute.setUrl("");
        fileAttribute.setDecodedUrl("");
        fileAttribute.setWatermarkText("");
        // watermarkType;  水印类型 0 文字 1图片
        if ("0".equals(watermarkType)) {
            fileAttribute.setWatermarkText(watermarkText);
        }
        /**
         * 将文件添加到缓存并返回文件的状态码,然后解析文件内容 采用多线程
         */
        List<FileAttribute> fileAttributeList = new ArrayList<>();
       final Thread t1 = new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   fileAttributeList.add(fileAttribute);
                   cacheService.putFileAttributeCache(fileMd5,fileAttributeList);
               }catch (Exception e){
                 LOGGER.error("线程A执行异常:"+e);
               }
           }
       },"_A");
       final Thread t2 = new Thread(() -> {
           try {
               t1.join();
               /**
                * 开始执行t2
                * 对添加到缓存里的路径进行遍历解析
                */
                Map<String, List<FileAttribute>> fileAttributeCache = cacheService.getFileAttributeCache();
                // 对map遍历来实现文件的解析
               if (!fileAttributeCache.isEmpty()){
                   for (Map.Entry<String,List<FileAttribute>> entry : fileAttributeCache.entrySet()) {
                       String key = entry.getKey();
                       FileAttribute getfileAttribute = entry.getValue().get(0);
                       LOGGER.info("onlinePreview-->fileAttribute 的属性:" + getfileAttribute);
                       req.setAttribute("fileKey", req.getParameter("fileKey"));
                       model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
                       LOGGER.info("onlinePreview-->model 的属性:" + model);

                       FilePreview filePreview = previewFactory.get(getfileAttribute);
                       LOGGER.info("onlinePreview-->filePreview 的属性:" + filePreview);
                       List<String> imgUrlsList = filePreview.filePreviewHandleList(url, model, getfileAttribute,imgfile);
                       if (imgUrlsList.size() > 0 && imgUrlsList != null){
                           cacheService.cleanFileAttributeCache(key);
                       }
                   }
               }

           } catch (InterruptedException e) {
               LOGGER.error("线程B执行异常:"+e);
           }catch (WatermarkException e) {
               LOGGER.error("处理水印出现异常:"+e);
           } catch (IOException e) {
               LOGGER.error("处理文件出现异常:"+e);
           }
       },"_B");

       t1.run();
       t2.run();
       return new ObjectMapper().writeValueAsString(new ReturnResponse<>(200,"success",fileMd5));

    }


    /**
     * 根据id检验 异步上传文件 流程进度
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "inspectionSchedule", method = RequestMethod.GET)
    public String inspectionSchedule(String id) throws JsonProcessingException {
    /*    Map<String, List<FileAttribute>> fileAttributeCache = cacheService.getFileAttributeCache();
        if (!fileAttributeCache.isEmpty()){
            for (Map.Entry<String,List<FileAttribute>> map : fileAttributeCache.entrySet()){
                if (id.equals(map.getKey())){
                    return new ObjectMapper().writeValueAsString(new ReturnResponse<>(200,"上传解析待解析","1"));
                }
                return new ObjectMapper().writeValueAsString(new ReturnResponse<>(200,"上传解析成功","0"));
            }
        }*/
        if (cacheService.getFileAttributeCache().containsKey(id)){
            return new ObjectMapper().writeValueAsString(new ReturnResponse<>(200,"上传解析待解析","1"));
        }
        return new ObjectMapper().writeValueAsString(new ReturnResponse<>(200,"上传解析成功","0"));
    }






}
