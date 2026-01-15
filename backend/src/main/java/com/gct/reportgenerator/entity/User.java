package com.gct.reportgenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名（唯一）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码（BCrypt加密）
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 角色：ADMIN, DESIGNER, VIEWER
     */
    @Column(nullable = false, length = 20)
    private String role;
    
    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
