package com.gct.reportgenerator.util;

import com.gct.reportgenerator.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前用户上下文工具类
 * 
 * 用于在Controller中便捷获取当前登录用户信息
 * 
 * @author GCT Team
 * @since 1.0.0
 */
public class CurrentUserContext {
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        Long userId = (Long) request.getAttribute("currentUserId");
        
        if (userId == null) {
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return userId;
    }
    
    /**
     * 获取当前用户名
     * 
     * @return 用户名
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = getCurrentRequest();
        String username = (String) request.getAttribute("currentUsername");
        
        if (username == null) {
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return username;
    }
    
    /**
     * 获取当前用户角色
     * 
     * @return 角色
     */
    public static String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        String role = (String) request.getAttribute("currentUserRole");
        
        if (role == null) {
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return role;
    }
    
    /**
     * 检查当前用户是否为管理员
     * 
     * @return true-是管理员，false-不是管理员
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }
    
    /**
     * 检查当前用户是否为设计者
     * 
     * @return true-是设计者，false-不是设计者
     */
    public static boolean isDesigner() {
        String role = getCurrentUserRole();
        return "ADMIN".equals(role) || "DESIGNER".equals(role);
    }
    
    /**
     * 获取当前HTTP请求
     * 
     * @return HttpServletRequest
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            throw new BusinessException("SYSTEM_ERROR", "无法获取请求上下文");
        }
        
        return attributes.getRequest();
    }
}