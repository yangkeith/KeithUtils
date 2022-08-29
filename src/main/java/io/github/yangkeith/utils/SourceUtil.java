package io.github.yangkeith.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @program: KeithUtils
 * @package: io.github.yangkeith.utils
 * @description:
 * @author: Keith
 * @date：Created in 2022-08-29 15:11
 */
public class SourceUtil {
    
    public static URL getSourceDir() {
        return getResource("");
    }
    public static String getPackageName(URL sourceDir) {
        if (sourceDir == null) {
            return "";
        }
        return sourceDir.getPath().substring(sourceDir.getPath().lastIndexOf(File.separator) + 1);
    }
    
    public static InputStream getResourceAsStream(String resourceName) {
        return  Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }
    public static URL getResource(String name){
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
}
