package cn.keking.service.impl;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.utils.DownloadUtils;
import cn.keking.utils.FileUtils;
import cn.keking.utils.PdfUtils;
import cn.keking.utils.SystemTypeUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import cn.keking.watermarkprocessor.WatermarkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * Created by kl on 2018/1/17.
 * Content :处理pdf文件
 */
@Service
public class PdfFilePreviewImpl implements FilePreview{

    @Value("${wartermark.text}")
    private String wartermarkText;

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

    String fileDir = ConfigConstants.getFileDir();

    public static final Logger LOGGER = LoggerFactory.getLogger(PdfFilePreviewImpl.class);

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) throws WatermarkException {
        String suffix=fileAttribute.getSuffix();
        String fileName=fileAttribute.getName();
        String officePreviewType = model.asMap().get("officePreviewType") == null ? ConfigConstants.getOfficePreviewType() : model.asMap().get("officePreviewType").toString();
        LOGGER.info("PdfFilePreviewImpl ---> officePreviewType:"+officePreviewType);
        String baseUrl = (String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl",0);
        model.addAttribute("pdfUrl", url);
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + "pdf";
        String outFilePath = fileDir + pdfName;
        LOGGER.info("PdfFilePreviewImpl ---> outFilePath:"+outFilePath);
        if (OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType) || OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_ALLIMAGES.equals(officePreviewType)) {
            //当文件不存在时，就去下载
            ReturnResponse<String> response = downloadUtils.downLoad(fileAttribute, fileName);
            if(!SystemTypeUtils.isOSLinux()){
                LOGGER.info("PdfFilePreviewImpl ---> wartermarkWinImagePath:"+wartermarkWinImagePath);
                File file = new File(outFilePath);
                File imageFile = new File(wartermarkWinImagePath);
                WatermarkProcessor.process(file, wartermarkText);
                WatermarkProcessor.process(file,imageFile);
            } else {
                LOGGER.info("PdfFilePreviewImpl ---> wartermarkLinuxImagePath:"+wartermarkLinuxImagePath);
                File file = new File(outFilePath);
                File imageFile = new File(wartermarkLinuxImagePath);
                WatermarkProcessor.process(file, wartermarkText);
                WatermarkProcessor.process(file,imageFile);
            }
            if (0 != response.getCode()) {
                model.addAttribute("fileType", suffix);
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            outFilePath = response.getContent();
            List<String> imageUrls = pdfUtils.pdf2jpg(outFilePath, pdfName, baseUrl,fileAttribute);
            LOGGER.info("PdfFilePreviewImpl ---> imageUrls:"+imageUrls);
            if (imageUrls == null || imageUrls.size() < 1) {
                model.addAttribute("msg", "pdf转图片异常，请联系管理员");
                model.addAttribute("fileType",fileAttribute.getSuffix());
                return "fileNotSupported";
            }
            model.addAttribute("imgurls", imageUrls);
            model.addAttribute("currentUrl", imageUrls.get(0));
            if (OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)) {
                return "officePicture";
            } else {
                return "picture";
            }
        }
        return "pdf";
    }

    @Override
    public List<String> filePreviewHandleList(String url, Model model, FileAttribute fileAttribute, MultipartFile imgFile) throws WatermarkException {
        return null;
    }
}
