package cn.keking.watermarkprocessor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;
import cn.keking.utils.FileExtensionUtils;

/**
 * @author li jiafang
 * @create 2019/11/8 17:25
 * @describe:  实现了对图片，word，excel，ppt，pdf等格式的水印处理，支持文本水印和图片水印
 */

public class WatermarkProcessor {

    private static Map<String, File> map = new HashMap<String, File>();

    /**
     * 图片水印
     * @author eko.zhan at 2018年9月18日 上午9:48:57
     * @param file
     * @param imageFile
     * @throws WatermarkException
     */
    public static void process(File file, File imageFile) throws WatermarkException {
        AbstractProcessor processor = null;
        if (FileExtensionUtils.isWord(file.getName())) {
            processor = new WordProcessor(file, imageFile);
        } else if (FileExtensionUtils.isExcel(file.getName())) {
            processor = new ExcelProcessor(file, imageFile);
        } else if (FileExtensionUtils.isPdf(file.getName())) {
            processor = new PdfProcessor(file, imageFile);
        } else if (FileExtensionUtils.isImage(file.getName())) {
            processor = new ImageProcessor(file, imageFile);
        }
        if (processor!=null) {
            processor.process();
        }else {
            throw new WatermarkException("不支持文件格式为 " + FilenameUtils.getExtension(file.getName()) + " 的水印处理");
        }
    }
    /**
     * 文本水印
     * @author eko.zhan at 2018年9月18日 上午11:08:08
     * @param file
     * @param text
     * @throws WatermarkException
     */
    public static void process(File file, String text) throws WatermarkException {
        Assert.hasText(text, "水印文本不能为空");
        //通过 text 生成 Image File
        if (map.get(text)==null) {
            map.put(text, FontImage.createImage(text));
        }
        process(file, map.get(text));
    }


}