package com.gct.reportgenerator.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户请求
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    /**
     * 密码（可选，不修改则为null）
     */
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;
    
    /**
     * 角色
     */
    @Pattern(regexp = "^(ADMIN|DESIGNER|VIEWER)$", message = "角色必须是ADMIN、DESIGNER或VIEWER")
    private String role;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
}
