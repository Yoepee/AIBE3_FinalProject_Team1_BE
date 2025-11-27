package com.back.domain.reservation.dto;

import com.back.domain.reservation.common.ReservationStatus;

import java.util.HashMap;
import java.util.Map;

public record ReservationStatusResBody(
        Map<ReservationStatus, Integer> statusCounts,
        Integer totalCount
) {
    // Compact Constructor (유효성 검증)
    public ReservationStatusResBody {
        if (statusCounts == null) {
            statusCounts = new HashMap<>();
        }
        if (totalCount == null) {
            totalCount = 0;
        }
    }

    // 편의 메서드
    public Integer getCount(ReservationStatus status) {
        return statusCounts.getOrDefault(status, 0);
    }

    public boolean hasReservations() {
        return totalCount > 0;
    }
}
