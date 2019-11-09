package cn.keking.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(PdfUtils.class);

    @Autowired
    FileUtils fileUtils;

    /**
     *
     * @param pdfFilePath 生成的pdf文件路径E:\WondersGroup\OnlinePreviewOfOfficeDocuments\kkFileView\jodconverter-web\src\main\file\测试1 - 副本 (9).pdf
     * @param pdfName 测试1 - 副本 (9).pdf
     * @param baseUrl http://127.0.0.1:8012/
     * @return
     */
    public List<String> pdf2jpg(String pdfFilePath, String pdfName, String baseUrl) {
        List<String> imageUrls = new ArrayList<>();
        // 查看缓存中是否存在pdfFilePath路径下已经转换过的图片，如果存在，直接返回该url
        Integer imageCount = fileUtils.getConvertedPdfImage(pdfFilePath);
        String imageFileSuffix = ".jpg";
        /**
         * pdfFolder = pdfFolder
         * urlPrefix = http://127.0.0.1:8012/测试1 - 副本 (9)
         */
        String pdfFolder = pdfName.substring(0, pdfName.length() - 4);
        String urlPrefix = baseUrl + pdfFolder;
        if (imageCount != null && imageCount.intValue() > 0) {
            for (int i = 0; i < imageCount ; i++)
            imageUrls.add(urlPrefix + "/" + i + imageFileSuffix);
            return imageUrls;
        }
        try {
            File pdfFile = new File(pdfFilePath);
            PDDocument doc = PDDocument.load(pdfFile);
            int pageCount = doc.getNumberOfPages();
            PDFRenderer pdfRenderer = new PDFRenderer(doc);

            int index = pdfFilePath.lastIndexOf(".");
            String folder = pdfFilePath.substring(0, index);

            File path = new File(folder);
            if (!path.exists()) {
                path.mkdirs();
            }
            /**
             * imageFilePath是图片生成的最终路径
             */
            String imageFilePath;
            // FIXME: 2019/11/8 这里完成将pdf转换成jpg图片
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                imageFilePath = folder + File.separator + pageIndex + imageFileSuffix;
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 105, ImageType.RGB);
                ImageIOUtil.writeImage(image, imageFilePath, 105);
                imageUrls.add(urlPrefix + "/" + pageIndex + imageFileSuffix);
            }
            doc.close();
            fileUtils.addConvertedPdfImage(pdfFilePath, pageCount);
        } catch (IOException e) {
            LOGGER.error("Convert pdf to jpg exception", e);
        }
        return imageUrls;
    }
}
