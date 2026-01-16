package com.gct.reportgenerator.repository;

import com.gct.reportgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象（可能为空）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据用户名查询启用的用户
     * 
     * @param username 用户名
     * @param enabled 是否启用
     * @return 用户对象（可能为空）
     */
    Optional<User> findByUsernameAndEnabled(String username, Boolean enabled);
    
    /**
     * 查询最大用户ID
     * 
     * @return 最大用户ID，如果没有用户则返回0
     */
    @Query("SELECT COALESCE(MAX(u.id), 0) FROM User u")
    Long findMaxId();
}
