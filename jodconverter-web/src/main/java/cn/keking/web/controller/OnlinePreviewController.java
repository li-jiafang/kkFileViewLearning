package cn.keking.web.controller;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.service.FilePreview;
import cn.keking.service.FilePreviewFactory;

import cn.keking.service.cache.CacheService;
import cn.keking.utils.FileUtils;
import cn.keking.watermarkprocessor.WatermarkException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author yudian-it
 */
@Controller
public class OnlinePreviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlinePreviewController.class);

    @Autowired
    FilePreviewFactory previewFactory;

    @Autowired
    CacheService cacheService;

    @Autowired
    private FileUtils fileUtils;

    private String fileDir = ConfigConstants.getFileDir();

    /**
     * 预览走用户体系，添加用户校验，满足要求后可以预览，不可以退出
     * 进入预览模式
     * @param url
     * @param model
     * @return
     */
    @RequestMapping(value = "/onlinePreview", method = RequestMethod.GET)
    public String onlinePreview(String url, Model model, HttpServletRequest req) throws WatermarkException {
        /**
         * 传入url=http://127.0.0.1:8012/demo/测试1 - 副本 (9).docx
         */
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        /**
         * fileAttribute 的所有属性
         * type = office
         * suffix = docx
         * name = 测试1 - 副本 (9).docx
         * url = http://127.0.0.1:8012/demo/测试1 - 副本 (9).docx
         * decodeUrl = http://127.0.0.1:8012/demo/测试1 - 副本 (9).docx
         */
        req.setAttribute("fileKey", req.getParameter("fileKey"));
        model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
        /**
         * 已经解析过的文件
         * req.getParameter("fileKey")和req.getParameter("officePreviewType")为null
         */
        FilePreview filePreview = previewFactory.get(fileAttribute);
        return filePreview.filePreviewHandle(url, model, fileAttribute);
    }


    @RequestMapping(value = "/picturesPreview")
    public String picturesPreview(Model model, HttpServletRequest req) throws UnsupportedEncodingException {
        String urls = req.getParameter("urls");
        String currentUrl = req.getParameter("currentUrl");
        // 路径转码
        String decodedUrl = URLDecoder.decode(urls, "utf-8");
        String decodedCurrentUrl = URLDecoder.decode(currentUrl, "utf-8");
        // 抽取文件并返回文件列表
        String[] imgs = decodedUrl.split("\\|");
        List imgurls = Arrays.asList(imgs);
        model.addAttribute("imgurls", imgurls);
        model.addAttribute("currentUrl",decodedCurrentUrl);
        return "picture";
    }
    /**
     * 根据url获取文件内容
     * 当pdfjs读取存在跨域问题的文件时将通过此接口读取
     *
     * @param urlPath
     * @param resp
     */
    @RequestMapping(value = "/getCorsFile", method = RequestMethod.GET)
    public void getCorsFile(String urlPath, HttpServletResponse resp) {
        InputStream inputStream = null;
        try {
            String strUrl = urlPath.trim();
            URL url = new URL(new URI(strUrl).toASCIIString());
            //打开请求连接
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = httpURLConnection.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            while (-1 != (len = inputStream.read(bs))) {
                resp.getOutputStream().write(bs, 0, len);
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("下载pdf文件失败", e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    /**
     * 通过api接口入队
     * @param url 请编码后在入队
     */
    @GetMapping("/addTask")
    @ResponseBody
    public String addQueueTask(String url) {
        cacheService.addQueueTask(url);
        return "success";
    }

}
