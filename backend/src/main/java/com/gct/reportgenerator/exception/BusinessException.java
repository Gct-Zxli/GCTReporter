package com.gct.reportgenerator.exception;

/**
 * 业务异常
 * 
 * @author GCT Team
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
