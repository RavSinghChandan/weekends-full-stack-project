-- =====================================================
-- Fullstack System Design App - Database Schema
-- MySQL 8.0 Database Schema
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS fullstack_system_design_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE fullstack_system_design_db;

-- =====================================================
-- Table Definitions
-- =====================================================

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN', 'MODERATOR') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    -- Indexes for performance
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_is_active (is_active),
    INDEX idx_users_created_at (created_at),
    INDEX idx_users_last_login (last_login)
);

-- User profiles table
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    phone VARCHAR(20),
    date_of_birth DATE,
    address JSON,
    preferences JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_profiles_user_id (user_id)
);

-- Sessions table
CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_sessions_user_id (user_id),
    INDEX idx_sessions_token_hash (token_hash),
    INDEX idx_sessions_expires_at (expires_at),
    INDEX idx_sessions_is_active (is_active),
    INDEX idx_sessions_created_at (created_at)
);

-- Audit logs table
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    resource_id BIGINT,
    details JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint (nullable for anonymous actions)
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_resource_type (resource_type),
    INDEX idx_audit_logs_resource_id (resource_id),
    INDEX idx_audit_logs_created_at (created_at),
    INDEX idx_audit_logs_ip_address (ip_address)
);

-- =====================================================
-- Additional Tables for Future Features
-- =====================================================

-- Password reset tokens table
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_password_reset_tokens_user_id (user_id),
    INDEX idx_password_reset_tokens_token_hash (token_hash),
    INDEX idx_password_reset_tokens_expires_at (expires_at),
    INDEX idx_password_reset_tokens_used (used)
);

-- Email verification tokens table
CREATE TABLE email_verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_email_verification_tokens_user_id (user_id),
    INDEX idx_email_verification_tokens_token_hash (token_hash),
    INDEX idx_email_verification_tokens_expires_at (expires_at),
    INDEX idx_email_verification_tokens_used (used)
);

-- User login attempts table (for security)
CREATE TABLE login_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    success BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_login_attempts_email (email),
    INDEX idx_login_attempts_ip_address (ip_address),
    INDEX idx_login_attempts_created_at (created_at),
    INDEX idx_login_attempts_success (success)
);

-- =====================================================
-- Views for Common Queries
-- =====================================================

-- User summary view
CREATE VIEW user_summary AS
SELECT 
    u.id,
    u.email,
    u.first_name,
    u.last_name,
    u.role,
    u.is_active,
    u.email_verified,
    u.created_at,
    u.last_login,
    up.avatar_url,
    up.bio,
    up.phone,
    COUNT(s.id) as active_sessions,
    COUNT(al.id) as audit_log_count
FROM users u
LEFT JOIN user_profiles up ON u.id = up.user_id
LEFT JOIN sessions s ON u.id = s.user_id AND s.is_active = TRUE
LEFT JOIN audit_logs al ON u.id = al.user_id
GROUP BY u.id, u.email, u.first_name, u.last_name, u.role, u.is_active, 
         u.email_verified, u.created_at, u.last_login, up.avatar_url, up.bio, up.phone;

-- Recent activity view
CREATE VIEW recent_activity AS
SELECT 
    al.id,
    al.action,
    al.resource_type,
    al.resource_id,
    al.details,
    al.ip_address,
    al.created_at,
    u.id as user_id,
    u.email,
    u.first_name,
    u.last_name
FROM audit_logs al
LEFT JOIN users u ON al.user_id = u.id
ORDER BY al.created_at DESC;

-- =====================================================
-- Stored Procedures
-- =====================================================

-- Procedure to clean up expired sessions
DELIMITER //
CREATE PROCEDURE cleanup_expired_sessions()
BEGIN
    DELETE FROM sessions WHERE expires_at < NOW();
    SELECT ROW_COUNT() as deleted_sessions;
END //
DELIMITER ;

-- Procedure to clean up old audit logs (older than 1 year)
DELIMITER //
CREATE PROCEDURE cleanup_old_audit_logs()
BEGIN
    DELETE FROM audit_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);
    SELECT ROW_COUNT() as deleted_audit_logs;
END //
DELIMITER ;

-- Procedure to get user statistics
DELIMITER //
CREATE PROCEDURE get_user_statistics()
BEGIN
    SELECT 
        COUNT(*) as total_users,
        COUNT(CASE WHEN is_active = TRUE THEN 1 END) as active_users,
        COUNT(CASE WHEN email_verified = TRUE THEN 1 END) as verified_users,
        COUNT(CASE WHEN role = 'ADMIN' THEN 1 END) as admin_users,
        COUNT(CASE WHEN role = 'MODERATOR' THEN 1 END) as moderator_users,
        COUNT(CASE WHEN last_login > DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as recent_users
    FROM users;
END //
DELIMITER ;

-- =====================================================
-- Triggers
-- =====================================================

-- Trigger to automatically create user profile when user is created
DELIMITER //
CREATE TRIGGER create_user_profile_trigger
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_profiles (user_id) VALUES (NEW.id);
END //
DELIMITER ;

-- Trigger to log user creation in audit logs
DELIMITER //
CREATE TRIGGER log_user_creation_trigger
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (user_id, action, resource_type, resource_id, details)
    VALUES (NEW.id, 'USER_CREATED', 'USER', NEW.id, 
            JSON_OBJECT('email', NEW.email, 'role', NEW.role));
END //
DELIMITER ;

-- Trigger to log user updates in audit logs
DELIMITER //
CREATE TRIGGER log_user_update_trigger
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.email != NEW.email OR OLD.role != NEW.role OR OLD.is_active != NEW.is_active THEN
        INSERT INTO audit_logs (user_id, action, resource_type, resource_id, details)
        VALUES (NEW.id, 'USER_UPDATED', 'USER', NEW.id, 
                JSON_OBJECT('old_email', OLD.email, 'new_email', NEW.email,
                           'old_role', OLD.role, 'new_role', NEW.role,
                           'old_active', OLD.is_active, 'new_active', NEW.is_active));
    END IF;
END //
DELIMITER ;

-- =====================================================
-- Initial Data
-- =====================================================

-- Insert default admin user (password: Admin123!)
INSERT INTO users (email, password_hash, first_name, last_name, role, is_active, email_verified)
VALUES (
    'admin@systemdesign.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iK8i', 
    'System', 
    'Administrator', 
    'ADMIN', 
    true, 
    true
);

-- Insert sample moderator user (password: Moderator123!)
INSERT INTO users (email, password_hash, first_name, last_name, role, is_active, email_verified)
VALUES (
    'moderator@systemdesign.com', 
    '$2a$12$Kj8mN9pQ2rS5tU7vW1xY3zA4bC6dE8fG0hI1jK2lM3nO4pQ5rS6tU7vW8xY9z', 
    'System', 
    'Moderator', 
    'MODERATOR', 
    true, 
    true
);

-- Insert sample regular user (password: User123!)
INSERT INTO users (email, password_hash, first_name, last_name, role, is_active, email_verified)
VALUES (
    'user@systemdesign.com', 
    '$2a$12$A1b2C3d4E5f6G7h8I9j0K1l2M3n4O5p6Q7r8S9t0U1v2W3x4Y5z6A7b8C9d0', 
    'Sample', 
    'User', 
    'USER', 
    true, 
    true
);

-- Update user profiles with sample data
UPDATE user_profiles SET 
    bio = 'System Administrator for Fullstack System Design App',
    phone = '+1234567890',
    preferences = JSON_OBJECT('theme', 'dark', 'notifications', true)
WHERE user_id = 1;

UPDATE user_profiles SET 
    bio = 'System Moderator for Fullstack System Design App',
    phone = '+1234567891',
    preferences = JSON_OBJECT('theme', 'light', 'notifications', true)
WHERE user_id = 2;

UPDATE user_profiles SET 
    bio = 'Sample user for testing purposes',
    phone = '+1234567892',
    preferences = JSON_OBJECT('theme', 'auto', 'notifications', false)
WHERE user_id = 3;

-- =====================================================
-- Database Maintenance
-- =====================================================

-- Create maintenance user with limited privileges
CREATE USER IF NOT EXISTS 'maintenance_user'@'localhost' IDENTIFIED BY 'maintenance_password';
GRANT SELECT, DELETE ON fullstack_system_design_db.sessions TO 'maintenance_user'@'localhost';
GRANT SELECT, DELETE ON fullstack_system_design_db.audit_logs TO 'maintenance_user'@'localhost';
GRANT EXECUTE ON PROCEDURE fullstack_system_design_db.cleanup_expired_sessions TO 'maintenance_user'@'localhost';
GRANT EXECUTE ON PROCEDURE fullstack_system_design_db.cleanup_old_audit_logs TO 'maintenance_user'@'localhost';

-- Create application user with full privileges
CREATE USER IF NOT EXISTS 'app_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON fullstack_system_design_db.* TO 'app_user'@'localhost';

-- Create read-only user for reporting
CREATE USER IF NOT EXISTS 'reporting_user'@'localhost' IDENTIFIED BY 'reporting_password';
GRANT SELECT ON fullstack_system_design_db.* TO 'reporting_user'@'localhost';

FLUSH PRIVILEGES;

-- =====================================================
-- Database Configuration
-- =====================================================

-- Set global variables for better performance
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL innodb_log_file_size = 268435456; -- 256MB
SET GLOBAL max_connections = 200;
SET GLOBAL query_cache_size = 67108864; -- 64MB

-- Show final configuration
SELECT 'Database schema created successfully!' as status;
SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema = 'fullstack_system_design_db'; 