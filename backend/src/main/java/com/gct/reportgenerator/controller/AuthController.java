package com.gct.reportgenerator.controller;

import com.gct.reportgenerator.dto.LoginRequest;
import com.gct.reportgenerator.dto.LoginResponse;
import com.gct.reportgenerator.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录接口
     * 
     * @param request 登录请求（用户名、密码）
     * @return 登录响应（token、用户信息）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求, 用户名: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("收到登出请求");
        
        authService.logout();
        
        return ResponseEntity.ok().build();
    }
}
