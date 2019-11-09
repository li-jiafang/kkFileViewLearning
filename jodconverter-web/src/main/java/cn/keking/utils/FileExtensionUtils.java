package cn.keking.utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
/**
 * @author li jiafang
 * @create 2019/11/8 17:29
 * @describe:
 */

public class FileExtensionUtils {

    private static String[] videoArray = new String[]{"mp4", "avi", "wma", "rmvb", "rm", "flash", "mid", "3gp"};
    private static String[] imageArray = new String[]{"jpg", "jpeg", "png", "gif", "ico", "bmp"};
    private static String[] audioArray = new String[]{"mp3", "wav", "ogg"};
    private static String[] officeArray = new String[]{"doc", "docx", "xls", "xlsx", "ppt", "pptx"};

    /**
     * 是否是视频文件
     *
     * @return
     * @author eko.zhan at 2017年12月21日 下午3:38:28
     */
    public static Boolean isVideo(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(videoArray, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是图片
     *
     * @param filename
     * @return
     * @author eko.zhan at 2017年12月21日 下午3:47:18
     */
    public static Boolean isImage(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(imageArray, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是Office文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Jan 3, 2018 5:30:52 PM
     */
    public static Boolean isOffice(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(officeArray, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是音频文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Jan 5, 2018 3:41:36 PM
     */
    public static Boolean isAudio(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(audioArray, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是Excel文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Aug 7, 2018 11:33:06 AM
     */
    public static Boolean isExcel(String filename) {
        String[] arr = new String[]{"xls", "xlsx"};
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(arr, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是Word文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Aug 7, 2018 11:33:06 AM
     */
    public static Boolean isWord(String filename) {
        String[] arr = new String[]{"doc", "docx"};
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(arr, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是 ppt 文件
     *
     * @param filename
     * @return
     * @author eko.zhan at 2018年9月1日 上午10:16:11
     */
    public static Boolean isPpt(String filename) {
        String[] arr = new String[]{"ppt", "pptx"};
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (ArrayUtils.contains(arr, extension)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是 Word 或 Excel 文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Aug 7, 2018 11:37:50 AM
     */
    public static Boolean isWordOrExcel(String filename) {
        return isWord(filename) || isExcel(filename);
    }

    /**
     * 是否是 office 或 pdf 文件
     *
     * @param filename
     * @return
     * @author eko.zhan at Aug 7, 2018 11:45:10 AM
     */
    public static Boolean isOfficeOrPdf(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (isOffice(filename) || "pdf".equals(extension)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 是否是 html 文件
     */
    public static Boolean isHtml(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if ("html".equals(extension) || "htm".equals(extension)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 是否是 pdf 文件
     */
    public static Boolean isPdf(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if ("pdf".equals(extension)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
