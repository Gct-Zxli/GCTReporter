package com.gct.reportgenerator.controller;

import com.gct.reportgenerator.util.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 
 * 用于测试Session管理和拦截器功能
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {
    
    /**
     * 获取当前用户信息（需要登录）
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Long userId = CurrentUserContext.getCurrentUserId();
        String username = CurrentUserContext.getCurrentUsername();
        String role = CurrentUserContext.getCurrentUserRole();
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("role", role);
        result.put("isAdmin", CurrentUserContext.isAdmin());
        result.put("isDesigner", CurrentUserContext.isDesigner());
        
        log.info("获取当前用户信息: {}", result);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试接口（需要登录）
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        String username = CurrentUserContext.getCurrentUsername();
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Hello " + username + ", this is a protected endpoint!");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 公开接口（无需登录）
     * 注意：这个接口路径不会被拦截器拦截，因为在AuthInterceptor中没有排除/api/v1/test/public
     * 实际会被拦截，这是为了测试拦截器是否工作
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "This is a public endpoint, but still requires login due to interceptor!");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(result);
    }
}