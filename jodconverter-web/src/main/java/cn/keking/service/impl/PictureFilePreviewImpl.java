package cn.keking.service.impl;

import cn.keking.model.FileAttribute;
import cn.keking.service.FilePreview;
import cn.keking.utils.FileUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by kl on 2018/1/17.
 * Content :图片文件处理 本项目展示弃用
 */
@Service
public class PictureFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileKey=(String) RequestContextHolder.currentRequestAttributes().getAttribute("fileKey",0);
        List imgUrls = Lists.newArrayList(url);
        try{
            imgUrls.clear();
            imgUrls.addAll(fileUtils.getRedisImgUrls(fileKey));
        }catch (Exception e){
            imgUrls = Lists.newArrayList(url);
        }
        model.addAttribute("imgurls", imgUrls);
        model.addAttribute("currentUrl",url);
        return "picture";
    }

    @Override
    public List<String> filePreviewHandleList(String url, Model model, FileAttribute fileAttribute, MultipartFile imgFile) throws WatermarkException {
        return null;
    }
}
