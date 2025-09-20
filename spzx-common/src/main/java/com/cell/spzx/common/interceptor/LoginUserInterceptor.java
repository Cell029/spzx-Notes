package com.cell.spzx.common.interceptor;

import com.cell.spzx.common.utils.AuthContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    // 配置 log
    private static final Logger log = Logger.getLogger(LoginUserInterceptor.class.getName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        log.info("拦截到请求: " + requestUri);
        // Long userId = (Long) request.getSession().getAttribute("userId");
        HttpSession session = request.getSession(false); // 不创建新 session
        Long userId = (session == null ? null : (Long) session.getAttribute("userId"));

        if (userId != null) {
            log.info("获取到用户 id：" + userId);
            AuthContextUtil.set(userId);
            return true;
        } else {
            log.log(Level.SEVERE,"未登录，请先完成登录操作！");
            response.sendRedirect("http://192.168.149.101:11000/login.html");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContextUtil.remove();  // 移除 ThreadLocal 中的数据
    }
}
