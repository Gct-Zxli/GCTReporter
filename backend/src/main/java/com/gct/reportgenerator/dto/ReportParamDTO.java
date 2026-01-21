package com.gct.reportgenerator.dto;

import lombok.Data;

@Data
public class ReportParamDTO {
    private String paramName;
    private String paramType; // STRING, NUMBER, DATE, BOOLEAN
    private Boolean required;
}
