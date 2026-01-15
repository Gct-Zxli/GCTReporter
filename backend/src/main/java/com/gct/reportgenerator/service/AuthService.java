package com.gct.reportgenerator.service;

import com.gct.reportgenerator.dto.LoginRequest;
import com.gct.reportgenerator.dto.LoginResponse;
import com.gct.reportgenerator.entity.User;
import com.gct.reportgenerator.exception.BusinessException;
import com.gct.reportgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 认证服务层
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录开始, 用户名: {}", request.getUsername());
        
        try {
            // 查询用户
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户名或密码错误"));
            
            // 检查用户是否启用
            if (!user.getEnabled()) {
                log.warn("用户已被禁用, 用户名: {}", request.getUsername());
                throw new BusinessException("USER_DISABLED", "账号已被禁用，请联系管理员");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("密码验证失败, 用户名: {}", request.getUsername());
                throw new BusinessException("INVALID_PASSWORD", "用户名或密码错误");
            }
            
            // 创建Session
            sessionService.createSession(user.getId(), user.getUsername(), user.getRole());
            
            // 生成Token（保留UUID方式用于前端localStorage）
            String token = "Bearer-" + UUID.randomUUID().toString();
            
            log.info("用户登录成功, 用户ID: {}, 用户名: {}, 角色: {}", 
                user.getId(), user.getUsername(), user.getRole());
            
            return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
                
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户登录失败, 用户名: {}, 错误信息: {}", 
                request.getUsername(), e.getMessage(), e);
            throw new BusinessException("LOGIN_FAILED", "登录失败，请稍后重试");
        }
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        try {
            Long userId = sessionService.getCurrentUserId();
            String username = sessionService.getCurrentUsername();
            
            log.info("用户登出开始, 用户ID: {}, 用户名: {}", userId, username);
            
            // 销毁Session
            sessionService.destroySession();
            
            log.info("用户登出成功, 用户ID: {}, 用户名: {}", userId, username);
            
        } catch (BusinessException e) {
            // Session不存在或已过期，记录警告但不抛出异常
            log.warn("登出时Session不存在: {}", e.getMessage());
        } catch (Exception e) {
            log.error("用户登出失败, 错误信息: {}", e.getMessage(), e);
            throw new BusinessException("LOGOUT_FAILED", "登出失败，请稍后重试");
        }
    }
}
