package com.gct.reportgenerator.controller;

import com.gct.reportgenerator.dto.ReportCreateRequest;
import com.gct.reportgenerator.dto.ReportDTO;
import com.gct.reportgenerator.dto.ReportColumnDTO;
import com.gct.reportgenerator.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 创建报表
     */
    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportCreateRequest request) {
        log.info("POST /api/v1/reports - Creating report: {}", request.getName());
        ReportDTO report = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * 获取报表列表
     */
    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        log.info("GET /api/v1/reports - Getting all reports");
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 获取报表完整配置
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<ReportDTO> getReportFull(@PathVariable Long id) {
        log.info("GET /api/v1/reports/{}/full - Getting full report configuration", id);
        ReportDTO report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    /**
     * 更新报表
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(
            @PathVariable Long id,
            @RequestBody ReportCreateRequest request) {
        log.info("PUT /api/v1/reports/{} - Updating report", id);
        ReportDTO report = reportService.updateReport(id, request);
        return ResponseEntity.ok(report);
    }

    /**
     * 删除报表
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        log.info("DELETE /api/v1/reports/{} - Deleting report", id);
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 检查报表名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Boolean>> checkNameExists(@RequestParam String name) {
        log.info("GET /api/v1/reports/check-name - Checking name: {}", name);
        boolean exists = reportService.isNameExists(name);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    /**
     * 从SQL提取列信息
     */
    @PostMapping("/extract-columns")
    public ResponseEntity<List<ReportColumnDTO>> extractColumns(@RequestBody ReportCreateRequest request) {
        log.info("POST /api/v1/reports/extract-columns - Extracting columns from SQL");
        List<ReportColumnDTO> columns = reportService.extractColumnsFromSql(
            request.getSqlContent(), 
            request.getParams()
        );
        return ResponseEntity.ok(columns);
    }
}
