package com.back.domain.reservation.controller;

import com.back.BaseContainerIntegrationTest;
import com.back.domain.reservation.repository.ReservationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({
        "/sql/categories.sql",
        "/sql/regions.sql",
        "/sql/members.sql",
        "/sql/posts.sql",
        "/sql/reservations.sql",
        "/sql/reviews.sql",
        "/sql/notifications.sql"
})
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ReservationControllerTest extends BaseContainerIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약 등록 테스트")
    @WithUserDetails("user3@example.com")
    void createReservationTest() throws Exception {
        LocalDateTime reservationStartAt = LocalDateTime.now().plusDays(30);
        LocalDateTime reservationEndAt = LocalDateTime.now().plusDays(31);

        String startAtStr = reservationStartAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String endAtStr = reservationEndAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String reqBody = """
            {
              "receiveMethod": "DIRECT",
              "receiveAddress1": null,
              "receiveAddress2": null,
              "returnMethod": "DIRECT",
              "reservationStartAt": "%s",
              "reservationEndAt": "%s",
              "postId": 5,
              "optionIds": null
            }
            """.formatted(startAtStr, endAtStr);

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.status").value(201),
                        jsonPath("$.msg").exists(),
                        jsonPath("$.data.id").exists(),
                        jsonPath("$.data.postId").value(5),
                        jsonPath("$.data.status").value("PENDING_APPROVAL"),
                        jsonPath("$.data.receiveMethod").value("DIRECT"),
                        jsonPath("$.data.returnMethod").value("DIRECT"),
                        jsonPath("$.data.reservationStartAt").value(startAtStr),
                        jsonPath("$.data.reservationEndAt").value(endAtStr),
                        jsonPath("$.data.option").isArray(),
                        jsonPath("$.data.logs").isArray(),
                        jsonPath("$.data.createdAt").exists(),
                        jsonPath("$.data.modifiedAt").exists()
                );
    }

    @Test
    @WithUserDetails("user1@example.com")
    @DisplayName("사용자가 보낸 예약 목록 조회 테스트")
    void getSentReservationsTest() throws Exception {
        // 멤버1은 reservation ID: 1,2,3 을 가지고 있음
        mockMvc.perform(get("/api/v1/reservations/sent")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value(200),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$.data.page").exists(),
                        jsonPath("$.data.size").exists(),
                        jsonPath("$.data.totalElements").exists(),
                        jsonPath("$.data.totalPages").exists()
                );
    }
}