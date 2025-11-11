package com.back.domain.reservation.reservation.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.reservation.reservation.common.ReservationStatus;
import com.back.domain.reservation.reservation.dto.CreateReservationReqBody;
import com.back.domain.reservation.reservation.dto.ReservationDto;
import com.back.domain.reservation.reservation.dto.GuestReservationSummaryResBody;
import com.back.domain.reservation.reservation.entity.Reservation;
import com.back.domain.reservation.reservation.service.ReservationService;
import com.back.global.rsData.RsData;
import com.back.global.security.SecurityUser;
import com.back.standard.util.page.PagePayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final MemberService memberService;
//    private final PostService postService;

    @Transactional
    @PostMapping
    public RsData<Void> createReservation(
            @Valid @RequestBody CreateReservationReqBody ReqBody,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Member author = memberService.getById(securityUser.getId());

        Reservation reservation = reservationService.create(ReqBody, author);

        return RsData.success("%d번 예약이 생성되었습니다.".formatted(reservation.getId()));
    }

    @Transactional(readOnly = true)
    @GetMapping("/sent")
    public RsData<PagePayload<GuestReservationSummaryResBody>> getSentReservations(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PageableDefault(size = 1, page = 0)Pageable pageable,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) String keyword
            ) {
        Member author = memberService.getById(securityUser.getId());

        PagePayload<GuestReservationSummaryResBody> reservations = reservationService.getSentReservations(author, pageable, status, keyword);

        return RsData.success("게스트의 예약 목록 %d 페이지 출력".formatted(reservations.page().page()), reservations);
    }

//    @Transactional(readOnly = true)
//    @GetMapping("/received/{postId}")
//    public RsData<PagePayload<HostReservationSummaryResBody>> getReceivedReservations(
//            @AuthenticationPrincipal SecurityUser securityUser,
//            @PathVariable Long postId,
//            @PageableDefault(size = 1, page = 0)Pageable pageable,
//            @RequestParam(required = false) ReservationStatus status,
//            @RequestParam(required = false) String keyword
//    ) {
//        Member author = memberService.getById(securityUser.getId());
//        Post post = postService.getById(postId);
//        PagePayload<HostReservationSummaryResBody> reservations = reservationService.getReceivedReservations(post, author, pageable, status, keyword);
//
//        return RsData.success("호스트의 %d번 게시글 예약 목록 %d 페이지 출력".formatted(postId, reservations.page().page()), reservations);
//    }

    @Transactional(readOnly = true)
    @GetMapping("/{reservationId}")
    public RsData<ReservationDto> getReservationDetail(@PathVariable Long reservationId) {
        // TODO: logs 정보 가져오기 (service 에서 ReservationDto를 만들어 오는 방식 고려)
        Reservation reservation = reservationService.getById(reservationId);
        return RsData.success("%d번 예약 상세 정보 조회".formatted(reservationId), new ReservationDto(reservation));
    }
}
