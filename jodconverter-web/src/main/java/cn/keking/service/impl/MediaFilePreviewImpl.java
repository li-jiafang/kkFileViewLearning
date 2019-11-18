package cn.keking.service.impl;

import cn.keking.model.FileAttribute;
import cn.keking.service.FilePreview;
import cn.keking.utils.FileUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : kl
 * @authorboke : kailing.pub
 * @create : 2018-03-25 上午11:58
 * @description:   本项目展示弃用
 **/
@Service
public class MediaFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        model.addAttribute("mediaUrl", url);
        String suffix=fileAttribute.getSuffix();
        if ("flv".equalsIgnoreCase(suffix)) {
            return "flv";
        }
        return "media";
    }

    @Override
    public List<String> filePreviewHandleList(String url, Model model, FileAttribute fileAttribute, MultipartFile imgFile) throws WatermarkException {
        return null;
    }


}
