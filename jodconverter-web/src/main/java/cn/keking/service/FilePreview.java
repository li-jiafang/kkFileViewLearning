package cn.keking.service;

import cn.keking.model.FileAttribute;
import cn.keking.watermarkprocessor.WatermarkException;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Created by kl on 2018/1/17.
 * Content :
 */
public interface FilePreview {
    String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) throws WatermarkException;
    List<String> filePreviewHandleList(String url, Model model, FileAttribute fileAttribute) throws WatermarkException;
}
