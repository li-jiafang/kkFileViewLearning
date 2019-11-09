package cn.keking.service.impl;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.utils.DownloadUtils;
import cn.keking.utils.FileUtils;
import cn.keking.utils.OfficeToPdf;
import cn.keking.utils.PdfUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import cn.keking.watermarkprocessor.WatermarkProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.util.List;

/**
 * Created by kl on 2018/1/17.
 * Content :处理office文件
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    PdfUtils pdfUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Autowired
    private OfficeToPdf officeToPdf;

    String fileDir = ConfigConstants.getFileDir();

    public static final String OFFICE_PREVIEW_TYPE_PDF = "pdf";
    public static final String OFFICE_PREVIEW_TYPE_IMAGE = "image";
    public static final String OFFICE_PREVIEW_TYPE_ALLIMAGES = "allImages";

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
                File file = new File(outFilePath);
                File imageFile = new File("E:\\万达信息图标.png");
                WatermarkProcessor.process(file, "万达信息专有");
                WatermarkProcessor.process(file,imageFile);
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
            List<String> imageUrls = pdfUtils.pdf2jpg(outFilePath, pdfName, baseUrl);
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

    /**
     * 完成给PDF添加水印
     */

}
