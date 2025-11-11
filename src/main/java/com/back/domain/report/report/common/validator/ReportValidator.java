package com.back.domain.report.report.common.validator;

import com.back.domain.report.report.common.ReportType;

public interface ReportValidator {
    boolean validateTargetId(ReportType reportType, Long targetId);
}
