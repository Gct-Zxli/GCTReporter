package com.gct.reportgenerator.interceptor;

import com.gct.reportgenerator.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 
 * 拦截需要登录的请求，验证用户Session
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    
    private final SessionService sessionService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("AuthInterceptor拦截请求: {} {}", method, requestURI);
        
        // 排除不需要认证的路径
        if (isExcludePath(requestURI)) {
            log.debug("路径{}无需认证，直接放行", requestURI);
            return true;
        }
        
        // 检查用户是否已登录
        if (!sessionService.isAuthenticated()) {
            log.warn("用户未登录，拒绝访问: {} {}", method, requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"用户未登录\"}");
            return false;
        }
        
        // 获取用户信息并记录
        try {
            Long userId = sessionService.getCurrentUserId();
            String username = sessionService.getCurrentUsername();
            String role = sessionService.getCurrentUserRole();
            
            // 将用户信息设置到request attribute，供Controller使用
            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentUserRole", role);
            
            log.debug("用户认证通过，允许访问: {} {}, 用户: {}({})", 
                method, requestURI, username, role);
                
        } catch (Exception e) {
            log.error("获取Session用户信息失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"认证信息无效\"}");
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断路径是否需要排除认证
     * 
     * @param path 请求路径
     * @return true-需要排除，false-需要认证
     */
    private boolean isExcludePath(String path) {
        // 登录接口
        if ("/api/v1/auth/login".equals(path)) {
            return true;
        }
        
        // 静态资源
        if (path.startsWith("/static/") || 
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/") ||
            path.endsWith(".ico")) {
            return true;
        }
        
        // Actuator健康检查
        if (path.startsWith("/actuator/")) {
            return true;
        }
        
        // 开发环境：Swagger文档（如果有）
        if (path.startsWith("/v3/api-docs") || 
            path.startsWith("/swagger-ui") || 
            path.equals("/swagger-ui.html")) {
            return true;
        }
        
        return false;
    }
}