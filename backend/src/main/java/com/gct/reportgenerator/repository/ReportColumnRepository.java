package com.gct.reportgenerator.repository;

import com.gct.reportgenerator.entity.ReportColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportColumnRepository extends JpaRepository<ReportColumn, Long> {
    List<ReportColumn> findByReportId(Long reportId);
    void deleteByReportId(Long reportId);
}
