package com.back.domain.report.report.common.validator;

import com.back.domain.report.report.common.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewReportValidator implements ReportValidator {

    @Override
    public boolean validateTargetId(ReportType reportType, Long targetId) {
        //TODO : ReviewRepository 주입받아 처리
        return reportType == ReportType.REVIEW;
    }
}
