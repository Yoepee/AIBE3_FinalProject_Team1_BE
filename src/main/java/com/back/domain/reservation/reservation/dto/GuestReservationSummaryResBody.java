package com.back.domain.reservation.reservation.dto;

import com.back.domain.reservation.reservation.common.ReservationDeliveryMethod;
import com.back.domain.reservation.reservation.common.ReservationStatus;
import com.back.domain.reservation.reservation.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GuestReservationSummaryResBody(
        Long reservationId,
        // Post post,
        ReservationStatus status,
        ReservationDeliveryMethod receiveMethod,
        ReservationDeliveryMethod returnMethod,
        String cancelReason,
        String rejectReason,
        LocalDate reservationStartAt,
        LocalDate reservationEndAt,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        // Option option,
        int totalAmount
) {
    public GuestReservationSummaryResBody(Reservation reservation) {
        this(
                reservation.getId(),
                // TODO: post 필드 매핑
                reservation.getStatus(),
                reservation.getReceiveMethod(),
                reservation.getReturnMethod(),
                reservation.getCancelReason(),
                reservation.getRejectReason(),
                reservation.getReservationStartAt(),
                reservation.getReservationEndAt(),
                reservation.getCreatedAt(),
                reservation.getModifiedAt(),
                // TODO: option 필드 매핑
                // TODO: totalAmount 계산
                0 // 임시 값, 실제 계산 로직으로 대체 필요
        );
    }
}
