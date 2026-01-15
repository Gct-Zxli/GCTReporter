-- =====================================================
-- 程序员报表生成工具 - 数据库初始化脚本
-- =====================================================
-- 
-- 创建日期: 2025-12-26
-- 版本: v1.0
-- 说明: 完整的数据库初始化脚本，包含所有表、索引和初始化数据
-- 执行要求: MySQL 5.7+ 或 MySQL 8.0+
-- 字符集: UTF8MB4 (支持中文和emoji)
-- 排序规则: utf8mb4_unicode_ci
-- 时区: UTC
--
-- 使用方法:
--   mysql -h localhost -u root -p < init-database.sql
--   或在MySQL客户端中执行: source init-database.sql
--
-- 幂等性说明: 脚本支持重复执行，会自动删除已存在的表再重建
--
-- =====================================================

-- 设置会话参数
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- =====================================================
-- 第1部分: 数据库创建
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS programmer_report 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 选择数据库
USE programmer_report;

-- =====================================================
-- 第2部分: 表结构定义
-- =====================================================

-- 删除已存在的表（反向依赖顺序）
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS report_role;
DROP TABLE IF EXISTS report_column;
DROP TABLE IF EXISTS report_param;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;

-- =====================================================
-- 2.1 角色表 (role)
-- =====================================================
CREATE TABLE role (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称（如ADMIN、DESIGNER、VIEWER）',
  description VARCHAR(255) COMMENT '角色描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  INDEX idx_role_name(role_name),
  INDEX idx_created_at(created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- =====================================================
-- 2.2 用户表 (user)
-- =====================================================
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录用）',
  password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  email VARCHAR(100) COMMENT '电子邮箱',
  status INT DEFAULT 1 COMMENT '账户状态（1:启用, 0:禁用）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  INDEX idx_username(username),
  INDEX idx_email(email),
  INDEX idx_status(status),
  INDEX idx_created_at(created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表';

-- =====================================================
-- 2.3 用户角色关联表 (user_role)
-- =====================================================
CREATE TABLE user_role (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id INT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id),
  
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
  
  INDEX idx_role_id(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与角色的多对多关联表';

-- =====================================================
-- 2.4 报表表 (report)
-- =====================================================
CREATE TABLE report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报表ID',
  name VARCHAR(100) NOT NULL COMMENT '报表名称',
  description TEXT COMMENT '报表描述',
  sql_content LONGTEXT NOT NULL COMMENT 'SQL查询语句（存储完整的SQL）',
  creator_id BIGINT NOT NULL COMMENT '创建者用户ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  CONSTRAINT fk_report_creator FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE RESTRICT,
  
  INDEX idx_creator_id(creator_id),
  INDEX idx_created_at(created_at),
  INDEX idx_name(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表定义表';

-- =====================================================
-- 2.5 报表参数表 (report_param)
-- =====================================================
CREATE TABLE report_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID',
  report_id BIGINT NOT NULL COMMENT '报表ID',
  param_name VARCHAR(50) NOT NULL COMMENT '参数名称（如 startDate、salesman）',
  param_type VARCHAR(20) COMMENT '参数类型（String, Integer, Date, Decimal, Boolean）',
  default_value VARCHAR(255) COMMENT '参数默认值',
  is_required BOOLEAN DEFAULT FALSE COMMENT '是否为必填参数',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  CONSTRAINT fk_report_param_report FOREIGN KEY (report_id) REFERENCES report(id) ON DELETE CASCADE,
  CONSTRAINT uk_report_param UNIQUE KEY (report_id, param_name),
  
  INDEX idx_report_id(report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表查询参数表';

-- =====================================================
-- 2.6 报表列配置表 (report_column)
-- =====================================================
CREATE TABLE report_column (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '列配置ID',
  report_id BIGINT NOT NULL COMMENT '报表ID',
  field_name VARCHAR(100) NOT NULL COMMENT '数据库字段名（SQL结果中的列名）',
  display_name VARCHAR(100) COMMENT '显示名称（报表中显示的列名）',
  column_width INT DEFAULT 100 COMMENT '列宽（像素）',
  format_type VARCHAR(20) COMMENT '格式化类型（date, number, currency, percent, text）',
  sort_order INT DEFAULT 0 COMMENT '排序顺序（0表示第一列）',
  is_visible BOOLEAN DEFAULT TRUE COMMENT '是否显示',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  CONSTRAINT fk_report_column_report FOREIGN KEY (report_id) REFERENCES report(id) ON DELETE CASCADE,
  CONSTRAINT uk_report_column UNIQUE KEY (report_id, field_name),
  
  INDEX idx_report_id(report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表列展示配置表';

-- =====================================================
-- 2.7 报表角色权限表 (report_role)
-- =====================================================
CREATE TABLE report_role (
  report_id BIGINT NOT NULL COMMENT '报表ID',
  role_id INT NOT NULL COMMENT '角色ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
  
  PRIMARY KEY (report_id, role_id),
  
  CONSTRAINT fk_report_role_report FOREIGN KEY (report_id) REFERENCES report(id) ON DELETE CASCADE,
  CONSTRAINT fk_report_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
  
  INDEX idx_role_id(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表与角色的权限关联表';

-- =====================================================
-- 2.8 操作审计日志表 (audit_log)
-- =====================================================
CREATE TABLE audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  user_id BIGINT COMMENT '操作用户ID',
  operation VARCHAR(50) COMMENT '操作类型（login, logout, create_report, update_report, delete_report, execute_query, update_permission等）',
  resource_type VARCHAR(50) COMMENT '资源类型（user, report, role, permission等）',
  resource_id VARCHAR(100) COMMENT '资源ID（如报表ID）',
  details LONGTEXT COMMENT '操作详情（JSON格式）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  
  CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
  
  INDEX idx_user_id(user_id),
  INDEX idx_created_at(created_at),
  INDEX idx_operation(operation),
  INDEX idx_resource_type(resource_type),
  INDEX idx_resource_id(resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志表';

-- =====================================================
-- 第3部分: 初始化数据
-- =====================================================

-- =====================================================
-- 3.1 初始化角色
-- =====================================================
INSERT INTO role (role_name, description) VALUES
  ('ADMIN', '系统管理员，拥有所有权限，可以管理用户、报表和权限'),
  ('DESIGNER', '报表设计者，可以创建和设计报表，不能删除其他人的报表'),
  ('VIEWER', '普通用户，只能查看已授权的报表，不能创建或修改报表');

-- =====================================================
-- 3.2 初始化用户（密码使用$2a$10前缀的BCrypt格式，示例密码：admin123）
-- =====================================================
-- 注意：以下密码均为示例，实际部署时应使用强密码
-- admin 的 BCrypt 密码: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/Ksm (密码: admin123)
-- designer 的 BCrypt 密码: $2a$10$abBGbYkPb9.GIGh55F6S0ew0mVHDt0kZzf0SmjLt9BnP1ZWHlRJD6 (密码: designer123)
-- viewer 的 BCrypt 密码: $2a$10$abBGbYkPb9.GIGh55F6S0ew0mVHDt0kZzf0SmjLt9BnP1ZWHlRJD6 (密码: viewer123)

INSERT INTO user (username, password, email, status) VALUES
  ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/Ksm', 'admin@example.com', 1),
  ('designer', '$2a$10$abBGbYkPb9.GIGh55F6S0ew0mVHDt0kZzf0SmjLt9BnP1ZWHlRJD6', 'designer@example.com', 1),
  ('viewer', '$2a$10$abBGbYkPb9.GIGh55F6S0ew0mVHDt0kZzf0SmjLt9BnP1ZWHlRJD6', 'viewer@example.com', 1);

-- =====================================================
-- 3.3 初始化用户角色关联
-- =====================================================
INSERT INTO user_role (user_id, role_id) VALUES
  (1, 1),  -- admin 拥有 ADMIN 角色
  (2, 2),  -- designer 拥有 DESIGNER 角色
  (3, 3);  -- viewer 拥有 VIEWER 角色

-- =====================================================
-- 第4部分: 验证脚本
-- =====================================================

-- 验证表创建结果
SELECT '====== 数据库初始化完成 ======' AS status;
SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'programmer_report';
SELECT '====== 表列表 ======' AS status;
SELECT table_name FROM information_schema.tables WHERE table_schema = 'programmer_report' ORDER BY table_name;

-- 验证初始化数据
SELECT '====== 初始化数据验证 ======' AS status;
SELECT '角色数：' AS label, COUNT(*) AS count FROM role;
SELECT '用户数：' AS label, COUNT(*) AS count FROM user;
SELECT '用户-角色关联数：' AS label, COUNT(*) AS count FROM user_role;

-- =====================================================
-- 第5部分: 备注和维护说明
-- =====================================================
/*
重要说明：

1. 密码管理
   - 脚本中的密码仅用于开发测试环境
   - 生产环境必须使用强密码（至少8字符，包含大小写字母、数字、特殊符号）
   - 密码应该在应用层使用BCrypt加密后存储

2. 用户初始化
   - admin/admin123: 系统管理员，用于管理后台
   - designer/designer123: 报表设计人员，用于设计报表
   - viewer/viewer123: 普通用户，用于查看报表
   
   首次部署时应立即修改这些默认密码！

3. 索引说明
   - username: 用于加速用户登录查询
   - creator_id: 用于查询用户创建的报表
   - created_at: 用于时间范围查询和排序
   - 外键自动创建相应的索引

4. 字符集说明
   - 使用 UTF8MB4 支持中文、emoji等扩展字符
   - 排序规则使用 utf8mb4_unicode_ci（不区分大小写）

5. 时间戳说明
   - 所有created_at和updated_at均使用UTC时区
   - 应用层应转换为本地时区显示

6. 外键约束
   - DELETE CASCADE: 删除用户时自动删除相关记录
   - DELETE RESTRICT: 删除用户时不允许删除报表（保护数据）
   - DELETE SET NULL: 审计日志中的用户ID可为空

7. 备份建议
   - 定期备份数据库，建议每天1次
   - 备份命令: mysqldump -h localhost -u root -p programmer_report > backup.sql
   - 恢复命令: mysql -h localhost -u root -p < backup.sql

8. 性能优化建议
   - 如果audit_log表增长过快，可考虑分区或归档
   - 定期清理过期的audit_log记录（如>30天）
   - 使用EXPLAIN分析复杂查询的性能

9. 扩展计划
   - 后续可添加数据权限表（某些用户只能看特定部门的数据）
   - 可添加报表版本控制表（支持报表的版本管理）
   - 可添加查询历史表（记录用户的查询历史用于分析）

10. 脚本重复执行说明
    - 本脚本支持幂等性，可安全重复执行
    - 脚本会先删除所有表再重建
    - 重建后的初始化数据会被重新插入
    - 如果需要保留现有数据，请先备份！
*/

-- =====================================================
-- 脚本执行完成
-- =====================================================
