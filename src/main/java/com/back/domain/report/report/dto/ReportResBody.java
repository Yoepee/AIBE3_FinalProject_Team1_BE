package com.back.domain.report.report.dto;

import com.back.domain.report.report.common.ReportType;
import com.back.domain.report.report.entity.Report;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReportResBody(
        Long reportId,
        ReportType reportType,
        Long targetId,
        String comment,
        Long authorId,
        LocalDateTime createdAt) {

    public static ReportResBody from(Report report) {
        return ReportResBody.builder()
                            .reportId(report.getId())
                            .reportType(report.getReportType())
                            .targetId(report.getTargetId())
                            .comment(report.getComment())
                            .authorId(report.getMember().getId())
                            .createdAt(report.getCreatedAt())
                            .build();
    }
}
