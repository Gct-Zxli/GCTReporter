package com.gct.reportgenerator.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseInitializationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testDatabaseTablesExist() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            
            // Check if all 6 tables exist using prepared statement
            String[] tables = {"users", "reports", "report_params", "report_columns", "report_permissions", "execution_logs"};
            
            for (String table : tables) {
                try (var stmt = conn.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
                    stmt.setString(1, table);
                    ResultSet rs = stmt.executeQuery();
                    assertTrue(rs.next(), "Table " + table + " should exist");
                    rs.close();
                }
            }
        }
    }

    @Test
    public void testFlywaySchemaHistoryExists() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flyway_schema_history");
            assertTrue(rs.next(), "flyway_schema_history table should exist");
            int count = rs.getInt(1);
            assertEquals(2, count, "flyway_schema_history should have 2 records");
            rs.close();
        }
    }

    @Test
    public void testTestAccountsExist() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            assertTrue(rs.next(), "users table should exist");
            int count = rs.getInt(1);
            assertEquals(3, count, "users table should have 3 test accounts");
            rs.close();
        }
    }

    @Test
    public void testTestAccountDetails() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT username, role, enabled FROM users ORDER BY id");
            
            // Check admin account
            assertTrue(rs.next());
            assertEquals("admin", rs.getString("username"));
            assertEquals("ADMIN", rs.getString("role"));
            assertEquals(1, rs.getInt("enabled"));
            
            // Check designer account
            assertTrue(rs.next());
            assertEquals("designer", rs.getString("username"));
            assertEquals("DESIGNER", rs.getString("role"));
            assertEquals(1, rs.getInt("enabled"));
            
            // Check viewer account
            assertTrue(rs.next());
            assertEquals("viewer", rs.getString("username"));
            assertEquals("VIEWER", rs.getString("role"));
            assertEquals(1, rs.getInt("enabled"));
            
            rs.close();
        }
    }

    @Test
    public void testPasswordEncryption() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT username, password FROM users WHERE username='admin'");
            assertTrue(rs.next(), "admin user should exist");
            
            String hashedPassword = rs.getString("password");
            assertNotNull(hashedPassword);
            assertTrue(hashedPassword.startsWith("$2a$"), "Password should be BCrypt encrypted");
            
            // Verify the password matches "admin123"
            assertTrue(passwordEncoder.matches("admin123", hashedPassword), 
                "admin123 should match the stored hash");
            assertFalse(passwordEncoder.matches("wrongpassword", hashedPassword), 
                "wrong password should not match");
            
            rs.close();
        }
    }

    @Test
    public void testTableStructure() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check users table has all required columns
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
            int columnCount = 0;
            boolean hasId = false, hasUsername = false, hasPassword = false, hasRole = false, 
                    hasEnabled = false, hasCreatedAt = false, hasUpdatedAt = false;
            
            while (rs.next()) {
                columnCount++;
                String columnName = rs.getString("name");
                if ("id".equals(columnName)) hasId = true;
                if ("username".equals(columnName)) hasUsername = true;
                if ("password".equals(columnName)) hasPassword = true;
                if ("role".equals(columnName)) hasRole = true;
                if ("enabled".equals(columnName)) hasEnabled = true;
                if ("created_at".equals(columnName)) hasCreatedAt = true;
                if ("updated_at".equals(columnName)) hasUpdatedAt = true;
            }
            
            assertEquals(7, columnCount, "users table should have 7 columns");
            assertTrue(hasId, "users table should have 'id' column");
            assertTrue(hasUsername, "users table should have 'username' column");
            assertTrue(hasPassword, "users table should have 'password' column");
            assertTrue(hasRole, "users table should have 'role' column");
            assertTrue(hasEnabled, "users table should have 'enabled' column");
            assertTrue(hasCreatedAt, "users table should have 'created_at' column");
            assertTrue(hasUpdatedAt, "users table should have 'updated_at' column");
            
            rs.close();
        }
    }
}
