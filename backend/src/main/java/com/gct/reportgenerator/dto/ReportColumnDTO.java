package com.gct.reportgenerator.dto;

import lombok.Data;

@Data
public class ReportColumnDTO {
    private String fieldName;
    private String displayName;
    private String formatType; // TEXT, NUMBER, DATE, CURRENCY, PERCENTAGE
}
