package com.gct.reportgenerator.service;

import com.gct.reportgenerator.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Session管理服务
 * 
 * 提供Session的创建、销毁、查询等功能
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    
    private static final String SESSION_USER_ID = "userId";
    private static final String SESSION_USERNAME = "username";
    private static final String SESSION_ROLE = "role";
    
    /**
     * 创建Session，保存用户信息
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     */
    public void createSession(Long userId, String username, String role) {
        HttpSession session = getHttpSession();
        session.setAttribute(SESSION_USER_ID, userId);
        session.setAttribute(SESSION_USERNAME, username);
        session.setAttribute(SESSION_ROLE, role);
        
        log.info("Session创建成功, 用户ID: {}, 用户名: {}, 角色: {}, SessionID: {}", 
            userId, username, role, session.getId());
    }
    
    /**
     * 销毁Session
     */
    public void destroySession() {
        try {
            HttpSession session = getHttpSession();
            String sessionId = session.getId();
            Long userId = (Long) session.getAttribute(SESSION_USER_ID);
            
            session.invalidate();
            
            log.info("Session销毁成功, 用户ID: {}, SessionID: {}", userId, sessionId);
        } catch (Exception e) {
            log.warn("Session销毁失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID
     * @throws BusinessException 如果Session不存在或用户未登录
     */
    public Long getCurrentUserId() {
        HttpSession session = getHttpSession();
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        
        if (userId == null) {
            log.warn("Session中未找到用户信息, SessionID: {}", session.getId());
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return userId;
    }
    
    /**
     * 获取当前登录用户名
     * 
     * @return 用户名
     * @throws BusinessException 如果Session不存在或用户未登录
     */
    public String getCurrentUsername() {
        HttpSession session = getHttpSession();
        String username = (String) session.getAttribute(SESSION_USERNAME);
        
        if (username == null) {
            log.warn("Session中未找到用户信息, SessionID: {}", session.getId());
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return username;
    }
    
    /**
     * 获取当前登录用户角色
     * 
     * @return 角色
     * @throws BusinessException 如果Session不存在或用户未登录
     */
    public String getCurrentUserRole() {
        HttpSession session = getHttpSession();
        String role = (String) session.getAttribute(SESSION_ROLE);
        
        if (role == null) {
            log.warn("Session中未找到用户信息, SessionID: {}", session.getId());
            throw new BusinessException("UNAUTHORIZED", "用户未登录");
        }
        
        return role;
    }
    
    /**
     * 检查用户是否已登录
     * 
     * @return true-已登录，false-未登录
     */
    public boolean isAuthenticated() {
        try {
            HttpSession session = getHttpSession();
            Long userId = (Long) session.getAttribute(SESSION_USER_ID);
            return userId != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取当前HTTP Session
     * 
     * @return HttpSession
     */
    private HttpSession getHttpSession() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            throw new BusinessException("SYSTEM_ERROR", "无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        return request.getSession(true);  // 如果不存在则创建
    }
}
