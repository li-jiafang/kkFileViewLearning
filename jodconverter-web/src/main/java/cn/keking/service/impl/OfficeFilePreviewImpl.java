package cn.keking.service.impl;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.utils.*;
import cn.keking.watermarkprocessor.WatermarkException;
import cn.keking.watermarkprocessor.WatermarkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kl on 2018/1/17.
 * Content :处理office文件
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    @Value("${wartermark.text}")
    private String wartermarkText;

    @Value("${fileOutPathName}")
    private String fileOutPathName;

    @Value("${wartermark.winimagepath}")
    private String wartermarkWinImagePath;

    @Value("${wartermark.linuximagepath}")
    private String wartermarkLinuxImagePath;

    @Autowired
    FileUtils fileUtils;

    @Autowired
    PdfUtils pdfUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Autowired
    private OfficeToPdf officeToPdf;

    String fileDir = ConfigConstants.getFileDir();

    public static final Logger LOGGER = LoggerFactory.getLogger(OfficeFilePreviewImpl.class);

    public static final String OFFICE_PREVIEW_TYPE_PDF = "pdf";
    public static final String OFFICE_PREVIEW_TYPE_IMAGE = "image";
    public static final String OFFICE_PREVIEW_TYPE_ALLIMAGES = "allImages";



    @Override
    public List<String> filePreviewHandleList(String url, Model model, FileAttribute fileAttribute, MultipartFile imgFile) throws WatermarkException, IOException {
        String officePreviewType = model.asMap().get("officePreviewType") == null ? ConfigConstants.getOfficePreviewType() : model.asMap().get("officePreviewType").toString();
        LOGGER.info("filePreviewHandle--->officePreviewType:"+officePreviewType);
        String baseUrl = (String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl",0);
        String suffix=fileAttribute.getSuffix();
        String fileName=fileAttribute.getName();
        // 获取文件的md5值
        String fileMd5 = fileAttribute.getFileMD5();
        // isHtml 判断是否是xls文件，如果是就转成html页，不是就转成其他模式
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        // pdfName = 测试1 - 副本 (9).pdf
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");
        /**
         * filePath是待解析文件路径
         * outFilePath是pdf生成后的文件路径
         * cachefilekey是缓存文件的key
         */
        String filePath = fileAttribute.getFilePath();
        String outFilePath = fileDir + fileOutPathName + pdfName;
        String cachefilekey = pdfName + fileAttribute.getFileMD5();

        /**
         * 如果文件名存在，md5相同，则直接读取缓存
         * fileUtils.listConvertedFiles().containsKey(pdfName) = true
         */
        if (!fileUtils.listConvertedFiles().containsKey(cachefilekey) || !ConfigConstants.isCacheEnabled()) {

            /**
             * 对上传的文件进行解析转pdf和jpg
             */
            if (StringUtils.hasText(outFilePath)) {
                //fixme 完成office转PDF
                officeToPdf.openOfficeToPDF(filePath, outFilePath);
            }
            /**
             * 处理完添加到缓存
             */
            if (ConfigConstants.isCacheEnabled()) {
                // 加入缓存
                fileUtils.addConvertedFile(cachefilekey, fileUtils.getRelativePath(outFilePath));
            }

        }

        /**
         * 转换成pdf后处理并添加水印
         */
        File file = new File(outFilePath);
        if (fileAttribute.getWatermarkText() != null){
            WatermarkProcessor.process(file,fileAttribute.getWatermarkText());
        }
        if (!imgFile.isEmpty() && !isHtml){
            InputStream ins = imgFile.getInputStream();
            File imageFile = new File(imgFile.getOriginalFilename());
            FileUtils.inputStreamToFile(ins, imageFile);
            WatermarkProcessor.process(file,imageFile);
            File del = new File(imageFile.toURI());
            del.delete();
        }
        /**
         * 对pdf文件再次处理获取图片
         */
        if (!isHtml && baseUrl != null && (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType) || OFFICE_PREVIEW_TYPE_ALLIMAGES.equals(officePreviewType))) {
            // imageUrls = http://127.0.0.1:8012/测试1 - 副本 (9)/0.jpg
            List<String> imageUrls = pdfUtils.pdf2jpg(outFilePath, pdfName, baseUrl,fileAttribute);
            LOGGER.info("filePreviewHandle--->imageUrls:"+imageUrls);

            if (imageUrls == null || imageUrls.size() < 1) {
                model.addAttribute("msg", "office转图片异常，请联系管理员");
                model.addAttribute("fileType",fileAttribute.getSuffix());
                //return "fileNotSupported";
                return new ArrayList<>();
            }
            model.addAttribute("imgurls", imageUrls);
            model.addAttribute("currentUrl", imageUrls.get(0));
            if (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)) {
                //return "officePicture";
                return new ArrayList<>();
            } else {
                //return "officePicture";
                return new ArrayList<>();
                //return "picture";
            }
        }
        return null;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) throws WatermarkException {
        // 预览Type，参数传了就取参数的，没传取系统默认
        /**
         * 点击预览时
         * model.asMap().get("officePreviewType") == null
         * ConfigConstants.getOfficePreviewType()=image
         * baseUrl = http://127.0.0.1:8012/
         * suffix = docx
         * fileName = 测试1 - 副本 (9).docx
         */
        String officePreviewType = model.asMap().get("officePreviewType") == null ? ConfigConstants.getOfficePreviewType() : model.asMap().get("officePreviewType").toString();
        LOGGER.info("filePreviewHandle--->officePreviewType:"+officePreviewType);
        /*if (OFFICE_PREVIEW_TYPE_ALLIMAGES.equals(officePreviewType)){
            return null;
        }*/
        String baseUrl = (String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl",0);
        String suffix=fileAttribute.getSuffix();
        String fileName=fileAttribute.getName();
        // isHtml 判断是否是xls文件，如果是就转成html页，不是就转成其他模式
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        // pdfName = 测试1 - 副本 (9).pdf
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");
        /**
         *  outFilePath是pdf生成后的文件路径
          */
        String outFilePath = fileDir + pdfName;
        LOGGER.info("filePreviewHandle--->outFilePath:"+outFilePath);
        // 通过redis缓存之前转换过的文件，然后判断是否已转换过，如果转换过，直接返回，否则执行转换
        if (!fileUtils.listConvertedFiles().containsKey(pdfName) || !ConfigConstants.isCacheEnabled()) {
            String filePath = fileDir + fileName;
            ReturnResponse<String> response = downloadUtils.downLoad(fileAttribute, null);
            if (0 != response.getCode()) {
                model.addAttribute("fileType", suffix);
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            filePath = response.getContent();
            if (StringUtils.hasText(outFilePath)) {
                //fixme 完成office转PDF
                officeToPdf.openOfficeToPDF(filePath, outFilePath);
                /**
                 * 转换成pdf后处理并添加水印
                 */

                System.out.println(wartermarkWinImagePath);
                if(!isHtml){
                    if(!SystemTypeUtils.isOSLinux()){
                        LOGGER.info("filePreviewHandle--->wartermarkWinImagePath:"+wartermarkWinImagePath);
                        File file = new File(outFilePath);
                        File imageFile = new File(wartermarkWinImagePath);
                        WatermarkProcessor.process(file, wartermarkText);
                        WatermarkProcessor.process(file,imageFile);
                    } else {
                        LOGGER.info("filePreviewHandle--->wartermarkLinuxImagePath:"+wartermarkLinuxImagePath);
                        File file = new File(outFilePath);
                        File imageFile = new File(wartermarkLinuxImagePath);
                        WatermarkProcessor.process(file, wartermarkText);
                        WatermarkProcessor.process(file,imageFile);
                    }

                }

                if (isHtml) {
                    // 对转换后的文件进行操作(改变编码方式)
                    fileUtils.doActionConvertedFile(outFilePath);
                }
                if (ConfigConstants.isCacheEnabled()) {
                    // 加入缓存
                    fileUtils.addConvertedFile(pdfName, fileUtils.getRelativePath(outFilePath));
                }
            }
        }

        // 对转换过的文件则获取url链接
        if (!isHtml && baseUrl != null && (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType) || OFFICE_PREVIEW_TYPE_ALLIMAGES.equals(officePreviewType))) {
            // imageUrls = http://127.0.0.1:8012/测试1 - 副本 (9)/0.jpg
            List<String> imageUrls = pdfUtils.pdf2jpg(outFilePath, pdfName, baseUrl,fileAttribute);
            LOGGER.info("filePreviewHandle--->imageUrls:"+imageUrls);
            if (imageUrls == null || imageUrls.size() < 1) {
                model.addAttribute("msg", "office转图片异常，请联系管理员");
                model.addAttribute("fileType",fileAttribute.getSuffix());
                return "fileNotSupported";
            }
            model.addAttribute("imgurls", imageUrls);
            model.addAttribute("currentUrl", imageUrls.get(0));
            if (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)) {
                return "officePicture";
            } else {
                return "officePicture";
                //return "picture";
            }
        }
        model.addAttribute("pdfUrl", pdfName);
        return isHtml ? "html" : "pdf";
    }




}
