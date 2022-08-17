package io.github.yangkeith.utils;

import com.jfinal.kit.LogKit;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>Title: QuietlyUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:21
 */
public class QuietlyUtil {
    /**
     * 静静地关闭
     *
     * @param closeables closeables
     * @author Keith
     * @date 2022-08-17
     */
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
    
    /**
     * 安静地睡觉
     *
     * @param millis 米尔斯
     * @author Keith
     * @date 2022-08-17
     */
    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogKit.error(e.toString(), e);
            Thread.currentThread().interrupt();
        }
    }
}
