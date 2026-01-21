package com.gct.reportgenerator.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SqlParamExtractorTest {

    @Test
    void testExtractParams_BasicSql() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND status = :status";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(2, params.size());
        assertTrue(params.contains("minAge"));
        assertTrue(params.contains("status"));
    }

    @Test
    void testExtractParams_DuplicateParams() {
        String sql = "SELECT * FROM orders WHERE price > :minPrice AND discount < :maxDiscount AND total > :minPrice";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        // 应该去重，但保留两个不同的参数
        assertEquals(2, params.size());
        assertTrue(params.contains("minPrice"));
        assertTrue(params.contains("maxDiscount"));
    }

    @Test
    void testExtractParams_NoParams() {
        String sql = "SELECT * FROM products";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertTrue(params.isEmpty());
    }

    @Test
    void testExtractParams_NullSql() {
        List<String> params = SqlParamExtractor.extractParams(null);
        assertTrue(params.isEmpty());
    }

    @Test
    void testExtractParams_EmptySql() {
        List<String> params = SqlParamExtractor.extractParams("");
        assertTrue(params.isEmpty());
    }

    @Test
    void testExtractParams_WithSingleLineComment() {
        String sql = "SELECT * FROM users WHERE age > :minAge -- :commentedParam\nAND status = :status";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(2, params.size());
        assertTrue(params.contains("minAge"));
        assertTrue(params.contains("status"));
        assertFalse(params.contains("commentedParam"));
    }

    @Test
    void testExtractParams_WithMultiLineComment() {
        String sql = "SELECT * FROM users WHERE age > :minAge /* :commentedParam */ AND status = :status";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(2, params.size());
        assertTrue(params.contains("minAge"));
        assertTrue(params.contains("status"));
        assertFalse(params.contains("commentedParam"));
    }

    @Test
    void testExtractParams_WithStringLiteral() {
        String sql = "SELECT * FROM users WHERE name = ':notAParam' AND age > :minAge";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(1, params.size());
        assertTrue(params.contains("minAge"));
        assertFalse(params.contains("notAParam"));
    }

    @Test
    void testExtractParams_ComplexSql() {
        String sql = """
            SELECT u.id, u.name, o.total
            FROM users u
            INNER JOIN orders o ON u.id = o.user_id
            WHERE u.age > :minAge
              AND u.department = :dept
              AND o.created_at BETWEEN :startDate AND :endDate
              -- AND u.status = :inactiveStatus (commented out)
              AND u.active = :isActive
            ORDER BY o.created_at DESC
            """;
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(5, params.size());
        assertTrue(params.contains("minAge"));
        assertTrue(params.contains("dept"));
        assertTrue(params.contains("startDate"));
        assertTrue(params.contains("endDate"));
        assertTrue(params.contains("isActive"));
        assertFalse(params.contains("inactiveStatus"));
    }

    @Test
    void testExtractParams_PreservesOrder() {
        String sql = "SELECT * FROM users WHERE a = :third AND b = :first AND c = :second";
        List<String> params = SqlParamExtractor.extractParams(sql);
        
        assertEquals(3, params.size());
        // 应该保持出现顺序
        assertEquals("third", params.get(0));
        assertEquals("first", params.get(1));
        assertEquals("second", params.get(2));
    }

    @Test
    void testValidateParams_AllMatch() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND status = :status";
        List<String> provided = Arrays.asList("minAge", "status");
        
        Map<String, List<String>> result = SqlParamExtractor.validateParams(sql, provided);
        
        assertTrue(result.get("missing").isEmpty());
        assertTrue(result.get("extra").isEmpty());
    }

    @Test
    void testValidateParams_MissingParams() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND status = :status";
        List<String> provided = Arrays.asList("minAge");
        
        Map<String, List<String>> result = SqlParamExtractor.validateParams(sql, provided);
        
        assertEquals(1, result.get("missing").size());
        assertTrue(result.get("missing").contains("status"));
        assertTrue(result.get("extra").isEmpty());
    }

    @Test
    void testValidateParams_ExtraParams() {
        String sql = "SELECT * FROM users WHERE age > :minAge";
        List<String> provided = Arrays.asList("minAge", "status", "dept");
        
        Map<String, List<String>> result = SqlParamExtractor.validateParams(sql, provided);
        
        assertTrue(result.get("missing").isEmpty());
        assertEquals(2, result.get("extra").size());
        assertTrue(result.get("extra").contains("status"));
        assertTrue(result.get("extra").contains("dept"));
    }

    @Test
    void testValidateParams_BothMissingAndExtra() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND dept = :department";
        List<String> provided = Arrays.asList("minAge", "status");
        
        Map<String, List<String>> result = SqlParamExtractor.validateParams(sql, provided);
        
        assertEquals(1, result.get("missing").size());
        assertTrue(result.get("missing").contains("department"));
        assertEquals(1, result.get("extra").size());
        assertTrue(result.get("extra").contains("status"));
    }

    @Test
    void testIsParamsMatch_True() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND status = :status";
        List<String> provided = Arrays.asList("minAge", "status");
        
        assertTrue(SqlParamExtractor.isParamsMatch(sql, provided));
    }

    @Test
    void testIsParamsMatch_False_Missing() {
        String sql = "SELECT * FROM users WHERE age > :minAge AND status = :status";
        List<String> provided = Arrays.asList("minAge");
        
        assertFalse(SqlParamExtractor.isParamsMatch(sql, provided));
    }

    @Test
    void testIsParamsMatch_False_Extra() {
        String sql = "SELECT * FROM users WHERE age > :minAge";
        List<String> provided = Arrays.asList("minAge", "extra");
        
        assertFalse(SqlParamExtractor.isParamsMatch(sql, provided));
    }
}
