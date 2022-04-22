package io.github.yangkeith.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * <p>Title: NetUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:18
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class NetUtil {
    
    public static String getLocalIpAddress() {
        String hostIpAddress = null;
        String siteLocalIpAddress = null;// 外网IP
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            boolean findSiteLocalIpAddress = false;// 是否找到外网IP
            while (networkInterfaces.hasMoreElements() && !findSiteLocalIpAddress) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    
                    if (!address.isSiteLocalAddress() && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 外网IP
                        siteLocalIpAddress = address.getHostAddress();
                        findSiteLocalIpAddress = true;
                        break;
                    } else if (address.isSiteLocalAddress()
                            && !address.isLoopbackAddress()
                            && address.getHostAddress().indexOf(":") == -1) {// 内网IP
                        hostIpAddress = address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 优先使用配置的外网IP地址
        return StrUtils.isNotBlank(siteLocalIpAddress) ? siteLocalIpAddress : hostIpAddress;
    }
}
