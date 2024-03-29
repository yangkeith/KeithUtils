package io.github.yangkeith.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: RequestUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:23
 */
public class RequestUtil {
    /**
     * 移动代理
     *
     * @see String[]
     */
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
    
    /**
     * ajax请求
     *
     * @param request 请求
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }
    
    /**
     * 是json内容类型
     *
     * @param request 请求
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public static boolean isJsonContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
    
    
    /**
     * 多部分请求
     *
     * @param request 请求
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
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
        if (StringUtil.isNotBlank(ua)) {
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
        return StringUtil.isNotBlank(ua) && ua.toLowerCase().contains("micromessenger");
    }
    
    
    /**
     * 是否是PC版的微信浏览器
     *
     * @param request
     * @return
     */
    public static boolean isWechatPcBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return StringUtil.isNotBlank(ua) && ua.toLowerCase().contains("windowswechat");
    }
    
    /**
     * 是否是IE浏览器
     *
     * @return
     */
    public static boolean isIEBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (StringUtil.isBlank(ua)) {
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
    
    /**
     * 获取知识产权地址
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
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
    
    /**
     * 获取用户代理
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
    
    /**
     * 获取推荐人
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }
    
    
    /**
     * 获取基地url
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getBaseUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        return request.getScheme() + "://" +
                request.getServerName() +
                (port == 80 || port == 443 ? "" : ":" + port) +
                request.getContextPath();
    }
    
    
    /**
     * 获取当前url
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String url = getBaseUrl(request) + request.getServletPath();
        if (StringUtil.isNotBlank(queryString)) {
            url = url.concat("?").concat(queryString);
        }
        return url;
    }
    
    
    /**
     * 获取当前编码url
     *
     * @param request 请求
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public static String getCurrentEncodeUrl(HttpServletRequest request) {
        return StringUtil.urlEncode(getCurrentUrl(request));
    }
}
