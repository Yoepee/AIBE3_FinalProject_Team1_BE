package com.back.domain.report.controller;

import com.back.BaseContainerIntegrationTest;
import com.back.WithMockMember;
import com.back.domain.category.entity.Category;
import com.back.domain.member.common.MemberRole;
import com.back.domain.member.entity.Member;
import com.back.domain.post.entity.Post;
import com.back.domain.report.common.ReportType;
import com.back.domain.report.dto.ReportReqBody;
import com.back.domain.report.entity.Report;
import com.back.domain.report.repository.ReportRepository;
import com.back.domain.reservation.entity.Reservation;
import com.back.domain.review.dto.ReviewWriteReqBody;
import com.back.domain.review.entity.Review;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.back.domain.post.common.ReceiveMethod.ANY;
import static com.back.domain.post.common.ReturnMethod.DELIVERY;
import static com.back.domain.reservation.common.ReservationDeliveryMethod.DIRECT;
import static com.back.domain.reservation.common.ReservationStatus.RETURN_COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest extends BaseContainerIntegrationTest {

    @Autowired EntityManager em;
    @Autowired ReportRepository reportRepository;

    @BeforeEach
    void setup() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        em.createNativeQuery("TRUNCATE TABLE review").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE post").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE category").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE member").executeUpdate();

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    @Test
    @DisplayName("POST 타입 신고 생성 성공")
    @WithMockMember
    void createReport_PostType_Success() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);
        Category category = Category.create("IT", null);
        em.persist(category);

        Member author = new Member("author@email.com", "test1234", "test-author", MemberRole.USER);
        em.persist(author);
        Post post = Post.of("title", "content", ANY, DELIVERY, "", "", 1000, 100, author, category);
        em.persist(post);

        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.POST,
                post.getId(),
                "부적절한 게시글입니다"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportReqBody))
               )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.reportType").value("POST"),
                        jsonPath("$.data.targetId").value(post.getId()),
                        jsonPath("$.data.comment").value(reportReqBody.comment())
                )
                .andDo(print());

        clearContext();

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.getFirst().getTargetId()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("MEMBER 타입 신고 생성 성공")
    @WithMockMember
    void createReport_MemberType_Success() throws Exception {
        // given
        Member reporter = new Member("test@email1.com", "test1234", "test-reporter", MemberRole.USER);
        em.persist(reporter);

        Member reported = new Member("test@email2.com", "test1234", "test-reported", MemberRole.USER);
        em.persist(reported);

        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.MEMBER,
                reported.getId(),
                "부적절한 사용자입니다"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody))
               )
               .andExpectAll(
                       status().isCreated(),
                       jsonPath("$.data.id").isNumber(),
                       jsonPath("$.data.reportType").value("MEMBER"),
                       jsonPath("$.data.targetId").value(reported.getId()),
                       jsonPath("$.data.comment").value(reportReqBody.comment())
               )
               .andDo(print());

        clearContext();

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.getFirst().getTargetId()).isEqualTo(reported.getId());
    }

    @Test
    @DisplayName("REVIEW 타입 신고 생성 성공")
    @WithMockMember
    void createReport_ReviewType_Success() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        Category category = Category.create("IT", null);
        em.persist(category);
        Post post = Post.of("title", "content", ANY, DELIVERY, "", "", 1000, 100, member, category);
        em.persist(post);

        Member author = new Member("author@email.com", "test1234", "test-author", MemberRole.USER);
        em.persist(author);
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation(RETURN_COMPLETED, DIRECT, "", "", DIRECT, now, now.plusDays(2), author, post);
        em.persist(reservation);

        Review review = Review.create(reservation, new ReviewWriteReqBody(1, 1, 1, "최악이었습니다"));
        em.persist(review);

        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.REVIEW,
                review.getId(),
                "부적절한 리뷰입니다"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody))
               )
               .andExpectAll(
                       status().isCreated(),
                       jsonPath("$.data.id").isNumber(),
                       jsonPath("$.data.reportType").value("REVIEW"),
                       jsonPath("$.data.targetId").value(review.getId()),
                       jsonPath("$.data.comment").value(reportReqBody.comment())
               )
               .andDo(print());

        clearContext();

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.getFirst().getTargetId()).isEqualTo(review.getId());
    }

    private void clearContext() {
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 신고 생성 실패")
    void createReport_Unauthorized() throws Exception {
        // given
        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.POST,
                100L,
                "Test comment"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody)))
               .andExpectAll(
                       status().isUnauthorized(),
                       jsonPath("$.status").value(401)
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 대상에 대한 신고 실패")
    @WithMockMember
    void createReport_TargetNotFound() throws Exception {
        // given
        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.POST,
                999L,
                "존재하지 않는 게시글 신고"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.status").value(404),
                       jsonPath("$.msg").value("존재하지 않는 게시글입니다.")
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("중복 신고 실패")
    @WithMockMember
    void createReport_DuplicateReport() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        Category category = Category.create("IT", null);
        em.persist(category);

        Member author = new Member("author@email.com", "test1234", "test-author", MemberRole.USER);
        em.persist(author);
        Post post = Post.of("title", "content", ANY, DELIVERY, "", "", 1000, 100, author, category);
        em.persist(post);

        Report report = Report.createPostType(post.getId(), "이미 신고됨", member);
        em.persist(report);

        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.POST,
                post.getId(),
                "중복 신고"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value("이미 신고한 대상입니다.")
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("본인 신고 실패")
    @WithMockMember
    void createReport_SelfReport() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody reportReqBody = new ReportReqBody(
                ReportType.MEMBER,
                1L,  // testUser 본인의 ID
                "본인 신고"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(reportReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value("본인은 신고할 수 없습니다.")
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("reportType만 null인 경우")
    @WithMockMember
    void createReport_NullReportType() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody invalidReqBody = new ReportReqBody(
                null,  // reportType null
                100L,
                "Valid comment"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value(containsString("reportType"))
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("targetId만 null인 경우")
    @WithMockMember
    void createReport_NullTargetId() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody invalidReqBody = new ReportReqBody(
                ReportType.POST,
                null,  // targetId null
                "Valid comment"
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value(containsString("targetId"))
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("comment가 빈 문자열인 경우")
    @WithMockMember
    void createReport_BlankComment() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody invalidReqBody = new ReportReqBody(
                ReportType.POST,
                100L,
                ""
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value(containsString("comment"))
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("comment가 공백만 있는 경우")
    @WithMockMember
    void createReport_WhitespaceComment() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody invalidReqBody = new ReportReqBody(
                ReportType.POST,
                100L,
                "   "
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").value(containsString("comment"))
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("잘못된 요청 본문으로 신고 생성 실패")
    @WithMockMember
    void createReport_InvalidRequestBody() throws Exception {
        // given
        Member member = new Member("test@email.com", "test1234", "test-member", MemberRole.USER);
        em.persist(member);

        ReportReqBody invalidReqBody = new ReportReqBody(
                null,
                null,
                ""
        );

        // when & then
        mockMvc.perform(post("/api/v1/reports")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidReqBody))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.status").value(400),
                       jsonPath("$.msg").exists(),
                       jsonPath("$.msg").value(containsString("comment")),
                       jsonPath("$.msg").value(containsString("reportType")),
                       jsonPath("$.msg").value(containsString("targetId")),
                       jsonPath("$.data").doesNotExist()
               )
               .andDo(print());

        assertThat(reportRepository.count()).isEqualTo(0);
    }
}