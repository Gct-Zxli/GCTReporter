package com.gct.reportgenerator.repository;

import com.gct.reportgenerator.entity.ReportParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportParamRepository extends JpaRepository<ReportParam, Long> {
    List<ReportParam> findByReportId(Long reportId);
    void deleteByReportId(Long reportId);
}
