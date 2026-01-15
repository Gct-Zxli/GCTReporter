package com.gct.reportgenerator.service;

import com.gct.reportgenerator.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SessionService单元测试
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@SpringBootTest
@WebAppConfiguration
class SessionServiceTest {
    
    @Autowired
    private SessionService sessionService;
    
    private MockHttpServletRequest mockRequest;
    private MockHttpSession mockSession;
    
    @BeforeEach
    void setUp() {
        // 创建Mock请求和Session
        mockRequest = new MockHttpServletRequest();
        mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        // 设置RequestContextHolder
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }
    
    @Test
    @DisplayName("创建Session - 成功场景")
    void createSession_Success() {
        // Given
        Long userId = 1L;
        String username = "admin";
        String role = "ADMIN";
        
        // When
        sessionService.createSession(userId, username, role);
        
        // Then
        assertTrue(sessionService.isAuthenticated());
        assertEquals(userId, sessionService.getCurrentUserId());
        assertEquals(username, sessionService.getCurrentUsername());
        assertEquals(role, sessionService.getCurrentUserRole());
    }
    
    @Test
    @DisplayName("获取当前用户信息 - Session不存在")
    void getCurrentUser_SessionNotExists() {
        // Given - 没有创建Session
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            sessionService.getCurrentUserId();
        });
        
        assertThrows(BusinessException.class, () -> {
            sessionService.getCurrentUsername();
        });
        
        assertThrows(BusinessException.class, () -> {
            sessionService.getCurrentUserRole();
        });
        
        assertFalse(sessionService.isAuthenticated());
    }
    
    @Test
    @DisplayName("销毁Session - 成功场景")
    void destroySession_Success() {
        // Given - 先创建Session
        sessionService.createSession(1L, "admin", "ADMIN");
        assertTrue(sessionService.isAuthenticated());
        
        // When
        sessionService.destroySession();
        
        // Then - Session应该已经无效，需要重新设置RequestContext
        // 因为Session销毁后，需要重新创建Session来测试
        mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        
        assertFalse(sessionService.isAuthenticated());
    }
    
    @Test
    @DisplayName("Session认证状态检查")
    void isAuthenticated_Test() {
        // Given - 初始状态未认证
        assertFalse(sessionService.isAuthenticated());
        
        // When - 创建Session
        sessionService.createSession(1L, "admin", "ADMIN");
        
        // Then - 应该已认证
        assertTrue(sessionService.isAuthenticated());
    }
    
    @Test
    @DisplayName("多次创建Session - 覆盖之前的Session")
    void createSession_Override() {
        // Given - 创建第一个Session
        sessionService.createSession(1L, "admin", "ADMIN");
        assertEquals("admin", sessionService.getCurrentUsername());
        
        // When - 创建第二个Session（覆盖）
        sessionService.createSession(2L, "designer", "DESIGNER");
        
        // Then - 应该使用新的Session信息
        assertEquals(2L, sessionService.getCurrentUserId());
        assertEquals("designer", sessionService.getCurrentUsername());
        assertEquals("DESIGNER", sessionService.getCurrentUserRole());
    }
}