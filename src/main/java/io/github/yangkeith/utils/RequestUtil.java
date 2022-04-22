package io.github.yangkeith.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: RequestUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:23
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class RequestUtil {
    static String[] mobileAgents = {"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
            "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod", "nokia",
            "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo",
            "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith",
            "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem", "wellcom", "bunjalloo",
            "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm",
            "jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav",
            "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
            "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g",
            "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
            "oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-",
            "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo",
            "teli", "tim-", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc",
            "winw", "winw", "xda", "xda-", "googlebot-mobile"};
    
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }
    
    public static boolean isJsonContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
    
    
    public static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("multipart");
    }
    
    /**
     * 是否是手机浏览器
     *
     * @return
     */
    public static boolean isMobileBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtils.isNotBlank(ua)) {
            ua = ua.toLowerCase();
            for (String mobileAgent : mobileAgents) {
                if (ua.indexOf(mobileAgent) > -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 是否是微信浏览器
     *
     * @return
     */
    public static boolean isWechatBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return StrUtils.isNotBlank(ua) && ua.toLowerCase().contains("micromessenger");
    }
    
    
    /**
     * 是否是PC版的微信浏览器
     *
     * @param request
     * @return
     */
    public static boolean isWechatPcBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return StrUtils.isNotBlank(ua) && ua.toLowerCase().contains("windowswechat");
    }
    
    /**
     * 是否是IE浏览器
     *
     * @return
     */
    public static boolean isIEBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StrUtils.isBlank(ua)) {
            return false;
        }
        
        ua = ua.toLowerCase();
        if (ua.contains("msie")) {
            return true;
        }
        
        if (ua.contains("gecko") && ua.contains("rv:11")) {
            return true;
        }
        
        return false;
    }
    
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        
        return ip;
    }
    
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
    
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }
    
    
    public static String getBaseUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        return request.getScheme() + "://" +
                request.getServerName() +
                (port == 80 || port == 443 ? "" : ":" + port) +
                request.getContextPath();
    }
    
    
    
    public static String getCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String url = getBaseUrl(request) + request.getServletPath();
        if (StrUtils.isNotBlank(queryString)) {
            url = url.concat("?").concat(queryString);
        }
        return url;
    }
    
    
    
    public static String getCurrentEncodeUrl(HttpServletRequest request) {
        return StrUtils.urlEncode(getCurrentUrl(request));
    }
}
