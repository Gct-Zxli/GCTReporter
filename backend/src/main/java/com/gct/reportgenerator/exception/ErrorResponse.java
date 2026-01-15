package com.gct.reportgenerator.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应
 * 
 * @author GCT Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * 错误代码
     */
    private String code;
    
    /**
     * 错误信息
     */
    private String message;
}
