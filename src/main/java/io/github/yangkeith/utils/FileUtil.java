package io.github.yangkeith.utils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * <p>Title: FileUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:10
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class FileUtil {
    public static String getSuffix(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return null;
    }
    
    
    public static String removePrefix(String src, String prefix) {
        if (src != null && src.startsWith(prefix)) {
            return src.substring(prefix.length());
        }
        return src;
    }
    
    
    public static String removeRootPath(String src) {
        return removePrefix(src, PathKit.getWebRootPath());
    }
    
    public static String readString(File file) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = fis.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), JFinal.me().getConstants().getEncoding());
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        } finally {
            close(fis, baos);
        }
        return null;
    }
    
    public static void writeString(File file, String string) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(string.getBytes(JFinal.me().getConstants().getEncoding()));
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        } finally {
            close(fos);
        }
    }
    
    public static void close(Closeable... closeable) {
        QuietlyUtil.closeQuietly(closeable);
    }
    
    
    public static Map<String, Object> unzip(String zipFilePath) throws Exception {
        String targetPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
        return unzip(zipFilePath, targetPath, true);
    }
    
    
    public static Map<String, Object> unzip(String zipFilePath, String targetPath) throws Exception {
        return unzip(zipFilePath, targetPath, true);
    }
    
    
    public static Map<String, Object> unzip(String zipFilePath, String targetPath, boolean safeUnzip) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("targetPath",targetPath);
        result.put("zipPath",zipFilePath);
        targetPath = getCanonicalPath(new File(targetPath));
        ZipFile zipFile = new ZipFile(zipFilePath, Charset.forName(EncodeUtil.getEncodeCode(zipFilePath)));
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            List<Map<String, String>> childFiles=new ArrayList<>();
            while (entryEnum.hasMoreElements()) {
                OutputStream os = null;
                InputStream is = null;
                try {
                    ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (!zipEntry.isDirectory()) {
                        if (safeUnzip && isNotSafeFile(zipEntry.getName())) {
                            continue;
                        }
                        File targetFile = new File(targetPath + File.separator + zipEntry.getName());
                        if (safeUnzip && !getCanonicalPath(targetFile).startsWith(targetPath)) {
                            continue;
                        }
                        Map<String, String> child = new HashMap<>();
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }
                        os = new BufferedOutputStream(Files.newOutputStream(targetFile.toPath()));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 4096)) > 0) {
                            os.write(buffer, 0, readLen);
                        }
                        String name = zipEntry.getName();
                        child.put("parentPath",targetFile.getParentFile().getCanonicalPath());
                        child.put("fileName",name);
                        child.put("filePath",getCanonicalPath(targetFile));
                        child.put("fileSimpleName",name.substring(0,name.lastIndexOf(".")));
                        child.put("fileExt",name.substring(name.lastIndexOf(".")+1));
                        childFiles.add(child);
                    }
                } finally {
                    close(is, os);
                }
            }
            result.put("children",childFiles);
        } finally {
            close(zipFile);
        }
        return result;
    }
    
    private static boolean isNotSafeFile(String name) {
        name = name.toLowerCase();
        return name.endsWith(".jsp") || name.endsWith(".jspx");
    }
    
    
    public static boolean isAbsolutePath(String path) {
        return StrUtils.isNotBlank(path) && (path.startsWith("/") || path.indexOf(":") > 0);
    }
    
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
