package com.gct.reportgenerator.controller;

import com.gct.reportgenerator.dto.ApiResponse;
import com.gct.reportgenerator.dto.CreateUserRequest;
import com.gct.reportgenerator.dto.UpdateUserRequest;
import com.gct.reportgenerator.dto.UserDTO;
import com.gct.reportgenerator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    @GetMapping
    public ApiResponse<List<UserDTO>> getAllUsers() {
        log.info("获取用户列表");
        List<UserDTO> users = userService.getAllUsers();
        return ApiResponse.success(users);
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        log.info("获取用户详情: id={}", id);
        UserDTO user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    /**
     * 创建用户
     * 
     * @param request 创建用户请求
     * @return 创建的用户
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户: {}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return ApiResponse.success(user, "用户创建成功");
    }
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param request 更新用户请求
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("更新用户: id={}", id);
        UserDTO user = userService.updateUser(id, request);
        return ApiResponse.success(user, "用户更新成功");
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return ApiResponse.success(null, "用户删除成功");
    }
}
