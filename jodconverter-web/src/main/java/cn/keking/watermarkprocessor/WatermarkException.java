package cn.keking.watermarkprocessor;

/**
 * @author li jiafang
 * @create 2019/11/8 17:44
 * @describe:
 */
@SuppressWarnings("serial")
public class WatermarkException extends Exception  {

    public WatermarkException(String msg) {
        super(msg);
    }

    public WatermarkException(String msg, Exception e) {
        super(msg, e);
    }

    public WatermarkException(String msg, Throwable t) {
        super(msg, t);
    }
}
