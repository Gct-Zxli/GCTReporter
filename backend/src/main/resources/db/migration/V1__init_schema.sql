-- V1__init_schema.sql
-- Create database schema for GCT Reporter

-- Users table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK(role IN ('ADMIN', 'DESIGNER', 'VIEWER')),
    enabled BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

-- Reports table
CREATE TABLE reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    sql_content TEXT NOT NULL,
    creator_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Create index on creator_id for faster lookups
CREATE INDEX idx_reports_creator_id ON reports(creator_id);
CREATE INDEX idx_reports_name ON reports(name);

-- Report parameters table
CREATE TABLE report_params (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    report_id INTEGER NOT NULL,
    param_name VARCHAR(50) NOT NULL,
    param_type VARCHAR(20) NOT NULL CHECK(param_type IN ('STRING', 'NUMBER', 'DATE', 'BOOLEAN')),
    required BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
);

-- Create index on report_id for faster lookups
CREATE INDEX idx_report_params_report_id ON report_params(report_id);

-- Report columns table
CREATE TABLE report_columns (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    report_id INTEGER NOT NULL,
    field_name VARCHAR(50) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    format_type VARCHAR(20) NOT NULL CHECK(format_type IN ('TEXT', 'NUMBER', 'DATE', 'CURRENCY', 'PERCENTAGE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
);

-- Create index on report_id for faster lookups
CREATE INDEX idx_report_columns_report_id ON report_columns(report_id);

-- Report permissions table
CREATE TABLE report_permissions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    report_id INTEGER NOT NULL,
    role VARCHAR(20) NOT NULL CHECK(role IN ('ADMIN', 'DESIGNER', 'VIEWER')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE,
    UNIQUE(report_id, role)
);

-- Create index on report_id for faster lookups
CREATE INDEX idx_report_permissions_report_id ON report_permissions(report_id);
CREATE INDEX idx_report_permissions_role ON report_permissions(role);

-- Execution logs table
CREATE TABLE execution_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    report_id INTEGER NOT NULL,
    execute_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
);

-- Create indexes for faster lookups
CREATE INDEX idx_execution_logs_user_id ON execution_logs(user_id);
CREATE INDEX idx_execution_logs_report_id ON execution_logs(report_id);
CREATE INDEX idx_execution_logs_execute_time ON execution_logs(execute_time);
