package com.gct.reportgenerator.interceptor;

import com.gct.reportgenerator.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthInterceptor集成测试
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthInterceptorIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private SessionService sessionService;
    
    private MockHttpSession mockSession;
    
    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession();
    }
    
    @Test
    @DisplayName("访问受保护接口 - 未登录返回401")
    void accessProtectedEndpoint_NotLoggedIn_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/test/current-user"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().json("{\"code\":\"UNAUTHORIZED\",\"message\":\"用户未登录\"}"));
    }
    
    @Test
    @DisplayName("访问登录接口 - 无需认证")
    void accessLoginEndpoint_NoAuthRequired() throws Exception {
        // 模拟登录请求（虽然会失败，但不会被拦截器阻拦）
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"test\"}"))
               .andExpect(status().is4xxClientError()); // 400或其他错误，但不是401
    }
    
    @Test
    @DisplayName("登录后访问受保护接口 - 成功")
    void accessProtectedEndpoint_LoggedIn_Success() throws Exception {
        // 首先模拟登录
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .session(mockSession))
               .andExpect(status().isOk());
        
        // 然后访问受保护的接口
        mockMvc.perform(get("/api/v1/test/current-user")
                .session(mockSession))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("admin"))
               .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    
    @Test
    @DisplayName("登录后登出再访问 - 返回401")
    void loginLogoutAccess_Returns401() throws Exception {
        // 1. 登录
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .session(mockSession))
               .andExpect(status().isOk());
        
        // 2. 验证登录成功
        mockMvc.perform(get("/api/v1/test/current-user")
                .session(mockSession))
               .andExpect(status().isOk());
        
        // 3. 登出
        mockMvc.perform(post("/api/v1/auth/logout")
                .session(mockSession))
               .andExpect(status().isOk());
        
        // 4. 再次访问应该返回401
        mockMvc.perform(get("/api/v1/test/current-user")
                .session(mockSession))
               .andExpect(status().isUnauthorized());
    }
}