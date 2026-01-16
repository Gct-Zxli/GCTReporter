package com.gct.reportgenerator.service;

import com.gct.reportgenerator.dto.CreateUserRequest;
import com.gct.reportgenerator.dto.UpdateUserRequest;
import com.gct.reportgenerator.dto.UserDTO;
import com.gct.reportgenerator.entity.User;
import com.gct.reportgenerator.exception.BusinessException;
import com.gct.reportgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务层
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 创建用户
     * 
     * @param request 创建用户请求
     * @return 用户DTO
     */
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        log.info("创建用户: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("USER_EXISTS", "用户名已存在");
        }
        
        // 加密密码
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        
        // 创建用户实体
        User user = User.builder()
                .username(request.getUsername())
                .password(encryptedPassword)
                .role(request.getRole())
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        
        // 保存用户
        User savedUser = userRepository.save(user);
        log.info("用户创建成功: id={}, username={}", savedUser.getId(), savedUser.getUsername());
        
        return convertToDTO(savedUser);
    }
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param request 更新用户请求
     * @return 用户DTO
     */
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        log.info("更新用户: id={}", id);
        
        // 查找用户
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在"));
        
        // 更新密码（如果提供）
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encryptedPassword);
        }
        
        // 更新角色
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        // 更新启用状态
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        log.info("用户更新成功: id={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: id={}", id);
        
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 删除用户
        userRepository.deleteById(id);
        log.info("用户删除成功: id={}", id);
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户DTO
     */
    public UserDTO getUserById(Long id) {
        log.debug("获取用户详情: id={}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在"));
        
        return convertToDTO(user);
    }
    
    /**
     * 获取所有用户
     * 
     * @return 用户DTO列表
     */
    public List<UserDTO> getAllUsers() {
        log.debug("获取所有用户列表");
        
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将User实体转换为UserDTO
     * 
     * @param user 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
