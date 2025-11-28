package com.back.global.app.mcp.tool;

import com.back.domain.reservation.repository.ReservationQueryRepository;
import com.back.global.app.mcp.dto.CategoryStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticTools {

    private final ReservationQueryRepository reservationQueryRepository;

    @Tool(description = """
            두 기간의 카테고리 통계를 비교합니다.
            ISO-8601 날짜 형식(YYYY-MM-DD)으로 기간을 지정하세요.
            사용자가 자연어(이번주, 지난주 등)로 요청하면 오늘 날짜를 기준으로 계산하여 변환하세요.
            """)
    public List<CategoryStatsDto> compareStats(
            @ToolParam(description = """
                    첫 번째 비교 기간의 시작 날짜 (ISO-8601 형식: YYYY-MM-DD)
                    
                    사용자가 자연어로 요청한 경우 오늘을 기준으로 다음과 같이 변환하세요:
                    - '오늘': 오늘 날짜
                    - '어제': 어제 날짜
                    - '이번주': 이번 주 월요일
                    - '지난주': 지난 주 월요일
                    - '이번달': 이번 달 1일
                    - '지난달': 지난 달 1일
                    """)
            LocalDate firstPeriod,

            @ToolParam(description = """
                    두 번째 비교 기간의 종료 날짜 (ISO-8601 형식: YYYY-MM-DD)
                    
                    자연어 변환 규칙 (오늘 기준):
                    - '오늘', '어제': 해당 날짜
                    - '이번주': 오늘 날짜
                    - '지난주': 지난 주 일요일
                    - '이번달': 오늘 날짜
                    - '지난달': 지난 달 마지막 날
                    """)
            LocalDate secondPeriod)
    {
        long start = System.currentTimeMillis();

        LocalDateTime from = firstPeriod.atStartOfDay();
        LocalDateTime to = secondPeriod.atTime(23, 59, 59);
        List<CategoryStatsDto> stats = reservationQueryRepository.getCategoryStats(from, to);

        long end = System.currentTimeMillis();
        log.info("카테고리별 통계 응답 시간: {}ms", (end - start));

        return stats;
    }
}
