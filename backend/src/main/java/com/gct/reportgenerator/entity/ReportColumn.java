package com.gct.reportgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "report_columns")
public class ReportColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "field_name", nullable = false, length = 50)
    private String fieldName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "format_type", nullable = false, length = 20)
    private String formatType; // TEXT, NUMBER, DATE, CURRENCY, PERCENTAGE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
