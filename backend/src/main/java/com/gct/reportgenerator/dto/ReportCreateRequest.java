package com.gct.reportgenerator.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReportCreateRequest {
    private String name;
    private String description;
    private String sqlContent;
    private List<ReportParamDTO> params;
    private List<ReportColumnDTO> columns;
}
