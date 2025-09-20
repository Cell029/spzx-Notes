package com.cell.spzx.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {
    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端真实IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = getHeaderIp(request, "X-Forwarded-For");
        if (ip == null) ip = getHeaderIp(request, "Proxy-Client-IP");
        if (ip == null) ip = getHeaderIp(request, "WL-Proxy-Client-IP");
        if (ip == null) ip = getHeaderIp(request, "HTTP_CLIENT_IP");
        if (ip == null) ip = getHeaderIp(request, "HTTP_X_FORWARDED_FOR");

        // 最后兜底
        if (ip == null) ip = request.getRemoteAddr();

        // 处理本地回环地址
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    private static String getHeaderIp(HttpServletRequest request, String header) {
        String ip = request.getHeader(header);
        if (isValidIp(ip)) {
            // X-Forwarded-For 可能有多个IP，取第一个
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            } else {
                return ip.trim();
            }
        }
        return null;
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
