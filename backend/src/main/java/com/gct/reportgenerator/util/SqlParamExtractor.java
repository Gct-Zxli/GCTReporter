package com.gct.reportgenerator.util;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL参数提取工具类
 * 从SQL语句中提取命名参数（:paramName格式）
 */
@Slf4j
public class SqlParamExtractor {

    // 匹配 :paramName 格式的命名参数
    private static final Pattern PARAM_PATTERN = Pattern.compile(":(\\w+)");
    
    // 匹配单行注释 --
    private static final Pattern SINGLE_LINE_COMMENT_PATTERN = Pattern.compile("--[^\n]*");
    
    // 匹配多行注释 /* */
    private static final Pattern MULTI_LINE_COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    
    // 匹配字符串字面量（单引号和双引号）
    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("'([^']*)'|\"([^\"]*)\"");

    /**
     * 从SQL语句中提取所有参数名称
     * 
     * @param sql SQL语句
     * @return 参数名称列表（去重且保持顺序）
     */
    public static List<String> extractParams(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Extracting parameters from SQL: {}", sql);

        // 1. 移除注释
        String cleanedSql = removeComments(sql);
        
        // 2. 移除字符串字面量（避免提取字符串中的:paramName）
        cleanedSql = removeStringLiterals(cleanedSql);

        // 3. 提取参数
        Set<String> paramNames = new LinkedHashSet<>(); // 使用LinkedHashSet保持顺序并去重
        Matcher matcher = PARAM_PATTERN.matcher(cleanedSql);
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            paramNames.add(paramName);
        }

        List<String> result = new ArrayList<>(paramNames);
        log.debug("Extracted {} parameters: {}", result.size(), result);
        
        return result;
    }

    /**
     * 移除SQL中的注释
     */
    private static String removeComments(String sql) {
        // 移除单行注释
        String result = SINGLE_LINE_COMMENT_PATTERN.matcher(sql).replaceAll("");
        
        // 移除多行注释
        result = MULTI_LINE_COMMENT_PATTERN.matcher(result).replaceAll("");
        
        return result;
    }

    /**
     * 移除SQL中的字符串字面量
     * 替换为占位符，避免字符串内容干扰参数提取
     */
    private static String removeStringLiterals(String sql) {
        return STRING_LITERAL_PATTERN.matcher(sql).replaceAll("''");
    }

    /**
     * 验证SQL中的参数是否与提供的参数列表匹配
     * 
     * @param sql SQL语句
     * @param providedParams 提供的参数名称列表
     * @return 包含missing（缺失）和extra（多余）参数的Map
     */
    public static Map<String, List<String>> validateParams(String sql, List<String> providedParams) {
        List<String> sqlParams = extractParams(sql);
        Set<String> providedSet = new HashSet<>(providedParams);
        Set<String> sqlParamSet = new HashSet<>(sqlParams);

        // 缺失的参数（SQL中有但未提供）
        List<String> missing = new ArrayList<>();
        for (String sqlParam : sqlParams) {
            if (!providedSet.contains(sqlParam)) {
                missing.add(sqlParam);
            }
        }

        // 多余的参数（提供了但SQL中没有）
        List<String> extra = new ArrayList<>();
        for (String provided : providedParams) {
            if (!sqlParamSet.contains(provided)) {
                extra.add(provided);
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("missing", missing);
        result.put("extra", extra);
        
        return result;
    }

    /**
     * 检查参数是否完全匹配
     */
    public static boolean isParamsMatch(String sql, List<String> providedParams) {
        Map<String, List<String>> validation = validateParams(sql, providedParams);
        return validation.get("missing").isEmpty() && validation.get("extra").isEmpty();
    }
}
