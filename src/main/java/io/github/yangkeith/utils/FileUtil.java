package io.github.yangkeith.utils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

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
 */
public class FileUtil {
    /**
     * 获取后缀
     *
     * @param fileName 文件名称
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getSuffix(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return null;
    }
    
    /**
     * 获取文件ext
     *
     * @param fileName 文件名称
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getFileExt(String fileName){
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        return null;
    }
    
    /**
     * 获取文件简单名字
     *
     * @param fileName 文件名称
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getFileSimpleName(String fileName){
        if (fileName != null && fileName.contains(".")) {
            if(fileName.contains(File.separator)){
                fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
            }
            return  fileName.substring(0,fileName.lastIndexOf("."));
        }
        return null;
    }
    
    /**
     * 删除前缀
     *
     * @param src    src
     * @param prefix 前缀
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String removePrefix(String src, String prefix) {
        if (src != null && src.startsWith(prefix)) {
            return src.substring(prefix.length());
        }
        return src;
    }
    
    
    /**
     * 删除根路径
     *
     * @param src src
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String removeRootPath(String src) {
        return removePrefix(src, PathKit.getWebRootPath());
    }
    
    public static String readString(InputStream inputStream){
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = inputStream.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), JFinal.me().getConstants().getEncoding());
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
        } finally {
            close(inputStream, baos);
        }
        return null;
    }
    
    /**
     * 读取字符串
     *
     * @param file 文件
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
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
    
    /**
     * 写字符串
     *
     * @param file   文件
     * @param string 字符串
     * @author Keith
     * @date 2022-08-17
     */
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
    
    /**
     * 关闭
     *
     * @param closeable closeable
     * @author Keith
     * @date 2022-08-17
     */
    public static void close(Closeable... closeable) {
        QuietlyUtil.closeQuietly(closeable);
    }
    
    
    /**
     * 解压缩
     *
     * @param zipFilePath zip文件路径
     * @return {@link Map }<{@link String }, {@link Object }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-17
     */
    public static Map<String, Object> unzip(String zipFilePath) throws Exception {
        String targetPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
        return unzip(zipFilePath, targetPath, true);
    }
    
    
    /**
     * 解压缩
     *
     * @param zipFilePath zip文件路径
     * @param targetPath  目标路径
     * @return {@link Map }<{@link String }, {@link Object }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-17
     */
    public static Map<String, Object> unzip(String zipFilePath, String targetPath) throws Exception {
        return unzip(zipFilePath, targetPath, true);
    }
    
    
    /**
     * 解压缩
     *
     * @param zipFilePath zip文件路径
     * @param targetPath  目标路径
     * @param safeUnzip   安全解压缩
     * @return {@link Map }<{@link String }, {@link Object }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-17
     */
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
    
    /**
     * 是不安全文件
     *
     * @param name 名字
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    private static boolean isNotSafeFile(String name) {
        name = name.toLowerCase();
        return name.endsWith(".jsp") || name.endsWith(".jspx");
    }
    
    
    /**
     * 是绝对路径
     *
     * @param path 路径
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public static boolean isAbsolutePath(String path) {
        return StringUtil.isNotBlank(path) && (path.startsWith("/") || path.indexOf(":") > 0);
    }
    
    /**
     * 获取规范路径
     *
     * @param file 文件
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 本地列表文件
     *
     * @param path 路径
     * @return {@link Collection }<{@link File }>
     * @author Keith
     * @date 2022-08-17
     */
    public static Collection<File> localListFiles(String path) {
        return localListFiles(new File(path));
    }
    
    /**
     * 遍历目录下的所有文件
     *
     * @param path 目录
     */
    public static Collection<File> localListFiles(File path) {
        Collection<File> fileCollection = new ArrayList<>();
        if (path != null && path.exists() && path.isDirectory()) {
            /**
             * targetDir：不要为 null、不要是文件、不要不存在
             * 第二个 文件过滤 参数如果为 FalseFileFilter.FALSE ，则不会查询任何文件
             * 第三个 目录过滤 参数如果为 FalseFileFilter.FALSE , 则只获取目标文件夹下的一级文件，而不会迭代获取子文件夹下的文件
             */
           //  fileCollection = FileUtils.listFiles(path, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            File[] files= path.listFiles();
            fileCollection.addAll(Arrays.asList(files));
        }
        return fileCollection;
    }
}
