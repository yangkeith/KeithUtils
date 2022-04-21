package top.keith.yang.utils;

import com.jfinal.kit.LogKit;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>Title: QuietlyUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:21
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class QuietlyUtil {
    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        LogKit.error(e.toString(), e);
                    }
                }
            }
        }
    }
    
    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogKit.error(e.toString(), e);
            Thread.currentThread().interrupt();
        }
    }
}
