package com.gct.reportgenerator.service;

import com.gct.reportgenerator.dto.*;
import com.gct.reportgenerator.entity.Report;
import com.gct.reportgenerator.entity.ReportColumn;
import com.gct.reportgenerator.entity.ReportParam;
import com.gct.reportgenerator.exception.BusinessException;
import com.gct.reportgenerator.repository.ReportColumnRepository;
import com.gct.reportgenerator.repository.ReportParamRepository;
import com.gct.reportgenerator.repository.ReportRepository;
import com.gct.reportgenerator.util.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final ReportParamRepository reportParamRepository;
    private final ReportColumnRepository reportColumnRepository;

    /**
     * 创建报表（带事务处理）
     */
    @Transactional
    public ReportDTO createReport(ReportCreateRequest request) {
        log.info("Creating report: {}", request.getName());
        
        // 验证当前用户
        Long currentUserId = CurrentUserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("AUTH_ERROR", "用户未登录");
        }

        // 创建报表基本信息
        Report report = new Report();
        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setSqlContent(request.getSqlContent());
        report.setCreatorId(currentUserId);
        
        // 保存报表
        report = reportRepository.save(report);
        log.info("Report saved with ID: {}", report.getId());

        // 保存参数配置
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            for (ReportParamDTO paramDTO : request.getParams()) {
                ReportParam param = new ReportParam();
                param.setReportId(report.getId());
                param.setParamName(paramDTO.getParamName());
                param.setParamType(paramDTO.getParamType());
                param.setRequired(paramDTO.getRequired() != null ? paramDTO.getRequired() : false);
                reportParamRepository.save(param);
            }
            log.info("Saved {} parameters for report {}", request.getParams().size(), report.getId());
        }

        // 保存列配置
        if (request.getColumns() != null && !request.getColumns().isEmpty()) {
            for (ReportColumnDTO columnDTO : request.getColumns()) {
                ReportColumn column = new ReportColumn();
                column.setReportId(report.getId());
                column.setFieldName(columnDTO.getFieldName());
                column.setDisplayName(columnDTO.getDisplayName());
                column.setFormatType(columnDTO.getFormatType());
                reportColumnRepository.save(column);
            }
            log.info("Saved {} columns for report {}", request.getColumns().size(), report.getId());
        }

        // 返回完整报表信息
        return getReportById(report.getId());
    }

    /**
     * 获取报表完整配置
     */
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("REPORT_NOT_FOUND", "报表不存在: " + id));

        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setName(report.getName());
        dto.setDescription(report.getDescription());
        dto.setSqlContent(report.getSqlContent());
        dto.setCreatorId(report.getCreatorId());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        // 加载参数
        List<ReportParam> params = reportParamRepository.findByReportId(id);
        dto.setParams(params.stream().map(param -> {
            ReportParamDTO paramDTO = new ReportParamDTO();
            paramDTO.setParamName(param.getParamName());
            paramDTO.setParamType(param.getParamType());
            paramDTO.setRequired(param.getRequired());
            return paramDTO;
        }).collect(Collectors.toList()));

        // 加载列配置
        List<ReportColumn> columns = reportColumnRepository.findByReportId(id);
        dto.setColumns(columns.stream().map(column -> {
            ReportColumnDTO columnDTO = new ReportColumnDTO();
            columnDTO.setFieldName(column.getFieldName());
            columnDTO.setDisplayName(column.getDisplayName());
            columnDTO.setFormatType(column.getFormatType());
            return columnDTO;
        }).collect(Collectors.toList()));

        return dto;
    }

    /**
     * 获取所有报表列表
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(report -> {
                    ReportDTO dto = new ReportDTO();
                    dto.setId(report.getId());
                    dto.setName(report.getName());
                    dto.setDescription(report.getDescription());
                    dto.setCreatorId(report.getCreatorId());
                    dto.setCreatedAt(report.getCreatedAt());
                    dto.setUpdatedAt(report.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新报表（带事务处理）
     */
    @Transactional
    public ReportDTO updateReport(Long id, ReportCreateRequest request) {
        log.info("Updating report: {}", id);
        
        // 验证报表存在
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("REPORT_NOT_FOUND", "报表不存在: " + id));

        // 更新基本信息
        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setSqlContent(request.getSqlContent());
        reportRepository.save(report);

        // 删除旧的参数配置
        reportParamRepository.deleteByReportId(id);
        reportParamRepository.flush();

        // 保存新的参数配置
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            for (ReportParamDTO paramDTO : request.getParams()) {
                ReportParam param = new ReportParam();
                param.setReportId(id);
                param.setParamName(paramDTO.getParamName());
                param.setParamType(paramDTO.getParamType());
                param.setRequired(paramDTO.getRequired() != null ? paramDTO.getRequired() : false);
                reportParamRepository.save(param);
            }
        }

        // 删除旧的列配置
        reportColumnRepository.deleteByReportId(id);
        reportColumnRepository.flush();

        // 保存新的列配置
        if (request.getColumns() != null && !request.getColumns().isEmpty()) {
            for (ReportColumnDTO columnDTO : request.getColumns()) {
                ReportColumn column = new ReportColumn();
                column.setReportId(id);
                column.setFieldName(columnDTO.getFieldName());
                column.setDisplayName(columnDTO.getDisplayName());
                column.setFormatType(columnDTO.getFormatType());
                reportColumnRepository.save(column);
            }
        }

        log.info("Report {} updated successfully", id);
        return getReportById(id);
    }

    /**
     * 删除报表（带级联删除）
     */
    @Transactional
    public void deleteReport(Long id) {
        log.info("Deleting report: {}", id);
        
        // 验证报表存在
        if (!reportRepository.existsById(id)) {
            throw new BusinessException("REPORT_NOT_FOUND", "报表不存在: " + id);
        }

        // 由于数据库设置了ON DELETE CASCADE，删除报表会自动删除相关的参数和列配置
        reportRepository.deleteById(id);
        
        log.info("Report {} deleted successfully", id);
    }
}
