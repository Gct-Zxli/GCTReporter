package com.gct.reportgenerator.repository;

import com.gct.reportgenerator.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatorId(Long creatorId);
    List<Report> findByNameContaining(String name);
}
