package com.gct.reportgenerator.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportDTO {
    private Long id;
    private String name;
    private String description;
    private String sqlContent;
    private Long creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReportParamDTO> params;
    private List<ReportColumnDTO> columns;
}
