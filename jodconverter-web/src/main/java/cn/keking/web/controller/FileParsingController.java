package cn.keking.web.controller;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.service.FilePreview;
import cn.keking.service.FilePreviewFactory;
import cn.keking.utils.FileMD5StringUtil;
import cn.keking.utils.FileUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.*;

import java.util.List;

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

    @Value("${base.url}")
    private String baseUrl;

    String demoDir = "upload";
    String demoPath = demoDir + File.separator;

    /**
     * 文件同步上传，上传后解析后返回图片地址
     * @param file
     * @param req
     * @return
     * @throws JsonProcessingException
     * @RequestParam("imgfile") MultipartFile imgfile,
     */
    @RequestMapping(value = "fileSynchronousUpload", method = RequestMethod.POST)
    public List<String> fileSynchronousUpload(@RequestParam("file") MultipartFile file,
                                              @RequestParam("imgfile") MultipartFile imgfile,
                                              String watermarkType,String watermarkText,
                                              Model model, HttpServletRequest req) throws IOException, WatermarkException {

        String fileName = saveAndGetFileName(file);

        /**
         * 读取新路径下的文件并开始解析文件类型
         */
        String url = baseUrl+"\\"+demoPath+fileName;
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        // 获取文件的md5值 文件内容没有改变则值不变，改变内容则值改变
        fileAttribute.setFileMD5(FileMD5StringUtil.getFileMD5String(file));
        fileAttribute.setName(fileName);
        fileAttribute.setFilePath(fileDir+demoPath+fileName);  // 获取上传文件的路径
        fileAttribute.setUrl("");
        fileAttribute.setDecodedUrl("");
        // watermarkType;  水印类型 0 文字 1图片
        if ("0".equals(watermarkType)){
            fileAttribute.setWatermarkText(watermarkText);
        }


        LOGGER.info("onlinePreview-->fileAttribute 的属性:"+fileAttribute);
        req.setAttribute("fileKey", req.getParameter("fileKey"));

        model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
        LOGGER.info("onlinePreview-->model 的属性:"+model);

        FilePreview filePreview = previewFactory.get(fileAttribute);
        LOGGER.info("onlinePreview-->filePreview 的属性:"+filePreview);
        List<String> s = filePreview.filePreviewHandleList(url, model, fileAttribute,imgfile);
        return s;

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
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "fileAsynchronousUpload", method = RequestMethod.POST)
    public String fileAsynchronousUpload(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request) throws JsonProcessingException {


        return null;
    }


    /**
     * 根据id检验 异步上传文件 流程进度
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "inspectionSchedule", method = RequestMethod.POST)
    public String inspectionSchedule(Long id) throws JsonProcessingException {


        return null;
    }






}
