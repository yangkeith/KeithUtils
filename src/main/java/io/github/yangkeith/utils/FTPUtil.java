package io.github.yangkeith.utils;

import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * @program: KeithUtils
 * @description: FTP
 * @author: Keith
 * @date：Created in 2022-05-05 14:43
 */
public class FTPUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 连接 FTP 服务器
     * @author Keith
     * @param addr  FTP 服务器 IP 地址
     * @param port FTP 服务器端口号
     * @param username 登录用户名
     * @param password 登录密码
     * @update 2022-05-05 14:55
     * @return FTPClient
     */
    public static FTPClient connectFtpServer(String addr, int port, String username, String password) {
        return connectFtpServer(addr, port, username, password,DEFAULT_CHARSET);
    }
    
    /**
     * 连接 FTP 服务器
     *
     * @param addr     FTP 服务器 IP 地址
     * @param port     FTP 服务器端口号
     * @param username 登录用户名
     * @param password 登录密码
     * @param controlEncoding 连接编码
     * @return
     * @throws Exception
     */
    public static FTPClient connectFtpServer(String addr, int port, String username, String password, String controlEncoding) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.setControlEncoding(controlEncoding);
            
            ftpClient.connect(addr, port);
            if (StringUtil.isBlank(username)) {
                ftpClient.login("Anonymous", "");
            } else {
                ftpClient.login(username, password);
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.abort();
                ftpClient.disconnect();
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(">>>>>FTP服务器连接登录失败，请检查连接参数是否正确，或者网络是否通畅*********");
        }
        return ftpClient;
    }
    
    /**
     * 使用完毕，应该及时关闭连接
     * 终止 ftp 传输
     * 断开 ftp 连接
     *
     * @param ftpClient ftp客户端
     * @return
     */
    public static FTPClient closeFTPConnect(FTPClient ftpClient) {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.abort();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }
    
    /**
     * 下载 FTP 服务器上指定的单个文件，而且本地存放的文件相对部分路径 会与 FTP 服务器结构保持一致
     *
     * @param ftpClient              ：连接成功有效的 FTP客户端连接
     * @param absoluteLocalDirectory ：本地存储文件的绝对路径，如 E:\gxg\ftpDownload
     * @param relativeRemotePath     ：ftpFile 文件在服务器所在的绝对路径，此方法强制路径使用右斜杠"\"，如 "\video\2018.mp4"
     * @return
     */
    public static void downloadSingleFile(FTPClient ftpClient, String absoluteLocalDirectory, String relativeRemotePath) {
        if (!ftpClient.isConnected() || !ftpClient.isAvailable()) {
            System.out.println(">>>>>FTP服务器连接已经关闭或者连接无效*********");
            return;
        }
        if (StringUtil.isBlank(absoluteLocalDirectory) || StringUtil.isBlank(relativeRemotePath)) {
            System.out.println(">>>>>下载时遇到本地存储路径或者ftp服务器文件路径为空，放弃...*********");
            return;
        }
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(relativeRemotePath);
            FTPFile ftpFile = null;
            if (ftpFiles.length >= 1) {
                ftpFile = ftpFiles[0];
            }
            if (ftpFile != null && ftpFile.isFile()) {
                File localFile = new File(absoluteLocalDirectory, relativeRemotePath);
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                OutputStream outputStream = new FileOutputStream(localFile);
                String workDir = relativeRemotePath.substring(0, relativeRemotePath.lastIndexOf("\\"));
                if (StringUtil.isBlank(workDir)) {
                    workDir = "/";
                }
                ftpClient.changeWorkingDirectory(workDir);
                ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                
                outputStream.flush();
                outputStream.close();
                System.out.println(">>>>>FTP服务器文件下载完毕*********" + ftpFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 遍历 FTP 服务器指定目录下的所有文件(包含子孙文件)
     *
     * @param ftpClient        ：连接成功有效的 FTP客户端连接
     * @param remotePath       ：查询的 FTP 服务器目录，如果文件，则视为无效，使用绝对路径，如"/"、"/video"、"\\"、"\\video"
     * @param relativePathList ：返回查询结果，其中为服务器目录下的文件相对路径，如：\1.png、\docs\overview-tree.html 等
     * @return
     */
    public static List<String> loopServerPath(FTPClient ftpClient, String remotePath, List<String> relativePathList) {
        if (!ftpClient.isConnected() || !ftpClient.isAvailable()) {
            System.out.println("ftp 连接已经关闭或者连接无效......");
            return relativePathList;
        }
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (FTPFile ftpFile : ftpFiles) {
                    if (ftpFile.isFile()) {
                        String relativeRemotePath = remotePath + "\\" + ftpFile.getName();
                        relativePathList.add(relativeRemotePath);
                    } else {
                        loopServerPath(ftpClient, remotePath + "\\" + ftpFile.getName(), relativePathList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relativePathList;
    }
    
    /**
     * 上传本地文件 或 目录 至 FTP 服务器
     *
     * @param ftpClient  连接成功有效的 FTPClient
     * @param uploadFile 待上传的文件 或 文件夹(此时会遍历逐个上传)
     * @throws Exception
     */
    public static void uploadFiles(FTPClient ftpClient, File uploadFile) {
        if (!ftpClient.isConnected() || !ftpClient.isAvailable()) {
            System.out.println(">>>>>FTP服务器连接已经关闭或者连接无效*****放弃文件上传****");
            return;
        }
        if (uploadFile == null || !uploadFile.exists()) {
            System.out.println(">>>>>待上传文件为空或者文件不存在*****放弃文件上传****");
            return;
        }
        try {
            if (uploadFile.isDirectory()) {
                ftpClient.makeDirectory(uploadFile.getName());
                ftpClient.changeWorkingDirectory(uploadFile.getName());
                
                File[] listFiles = uploadFile.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    File loopFile = listFiles[i];
                    if (loopFile.isDirectory()) {
                        uploadFiles(ftpClient, loopFile);
                        ftpClient.changeToParentDirectory();
                    } else {
                        FileInputStream input = new FileInputStream(loopFile);
                        boolean flag =ftpClient.storeFile(loopFile.getName(), input);
                        input.close();
                        if(flag){
                            System.out.println(">>>>>文件上传成功****" +loopFile.getPath());
                        }else {
                            System.out.println(">>>>>文件上传失败****" + loopFile.getPath());
                        }
                    }
                }
            } else {
                FileInputStream input = new FileInputStream(uploadFile);
                boolean flag = ftpClient.storeFile(uploadFile.getName(), input);
                input.close();
                if(flag){
                    System.out.println(">>>>>文件上传成功****" + uploadFile.getPath());
                }else {
                    System.out.println(">>>>>文件上传失败****" + uploadFile.getPath());
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 同步本地目录与 FTP 服务器目录
     * 1）约定：FTP 服务器有，而本地没有的，则下载下来；本地有，而ftp服务器没有的，则将本地多余的删除
     * 2）始终确保本地与 ftp 服务器内容一致
     * 2）让 FTP 服务器与 本地目录保持结构一致，如 服务器上是 /docs/overview-tree.html，则本地也是 localDir/docs/overview-tree.html
     *
     * @param ftpClient        连接成功有效的 FTPClient
     * @param FileDir ：与 FTP 目录进行同步的本地目录
     */
    public static void syncFiles(FTPClient ftpClient, String FileDir) throws IOException {
        if (!ftpClient.isConnected() || !ftpClient.isAvailable() || StringUtil.isBlank(FileDir)) {
            System.out.println(">>>>>FTP服务器连接已经关闭或者连接无效*********");
            return;
        }
        
        Collection<File> fileCollection = FileUtil.localListFiles(new File(FileDir));
        System.out.println(">>>>>本地存储目录共有文件数量*********" + fileCollection.size());
        
        List<String> relativePathList = new ArrayList<>();
        relativePathList = loopServerPath(ftpClient, "", relativePathList);
        System.out.println(">>>>>FTP 服务器端共有文件数量*********" + relativePathList.size());
        
        for (File localFile : fileCollection) {
            String localFilePath = localFile.getPath();
            String localFileSuffi = localFilePath.replace(FileDir, "");
            if (relativePathList.contains(localFileSuffi)) {
                FTPFile[] ftpFiles = ftpClient.listFiles(localFileSuffi);
                System.out.println(">>>>>本地文件 在 FTP 服务器已存在*********" + localFile.getPath());
                if (ftpFiles.length >= 1 && localFile.length() != ftpFiles[0].getSize()) {
                    downloadSingleFile(ftpClient, FileDir, localFileSuffi);
                    System.out.println(">>>>>本地文件与 FTP 服务器文件大小不一致，重新下载*********" + localFile.getPath());
                }
                relativePathList.remove(localFileSuffi);
            } else {
                System.out.println(">>>>>本地文件在 FTP 服务器不存在，删除本地文件*********" + localFile.getPath());
                localFile.delete();
                File parentFile = localFile.getParentFile();
                while (parentFile.list().length == 0) {
                    parentFile.delete();
                    parentFile = parentFile.getParentFile();
                }
            }
        }
        for (String s : relativePathList) {
            System.out.println(">>>>> FTP 服务器存在新文件，准备下载*********" + s);
            downloadSingleFile(ftpClient, FileDir, s);
        }
    }
    
    private static  String determineServerProtocol(String host, String port) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try (Socket socket = new Socket(host, Integer.parseInt(port))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            result = in.readLine();
            out.close();
            in.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        String s = null;
        if (result.contains("SSH")) {
            System.out.println("Server is SFTP");
            // do things...
        } else {
            System.out.println("Server is FTP");
            // do things...
        }
        return s;
    }
    
    public static void main(String[] args) throws JSchException, SftpException {
        // FTPClient client = FTPUtil.connectFtpServer("192.168.1.222",21,"map","sagis");
        // FTPUtil.uploadFiles(client,new File("D:\\Mine\\Desktop\\唐山疫情一张图接口文档V1 (1).pdf"));
        // List<String> files = new ArrayList<>();
        // FTPUtil.loopServerPath(client,"",files);
        // for (String file : files) {
        //     System.out.println(file);
        // }
        // FTPUtil.closeFTPConnect(client);
        determineServerProtocol("192.168.1.160","22");
        ChannelSftp client = new ChannelSftp();
        JSch jsch = new JSch();
        Session sshSession = jsch.getSession("root", "192.168.1.160", 22);
        // 添加s密码
        sshSession.setPassword("sagis");
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        // 开启sshSession链接
        sshSession.connect();
        // 获取sftp通道
        client = (ChannelSftp) sshSession.openChannel("sftp");
        Vector files = client.ls("/etc");
        for (Object file : files) {
            System.out.println(file);
        }
        // 开启
        client.connect();
    }
}
