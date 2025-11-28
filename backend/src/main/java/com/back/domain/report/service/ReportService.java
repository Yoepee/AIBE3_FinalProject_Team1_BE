package com.back.domain.report.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import com.back.domain.report.common.ReportType;
import com.back.domain.report.common.validator.ReportValidator;
import com.back.domain.report.dto.ReportReqBody;
import com.back.domain.report.dto.ReportResBody;
import com.back.domain.report.entity.Report;
import com.back.domain.report.repository.ReportQueryRepository;
import com.back.domain.report.repository.ReportRepository;
import com.back.domain.review.entity.Review;
import com.back.domain.review.repository.ReviewRepository;
import com.back.global.exception.ServiceException;
import com.back.standard.util.page.PagePayload;
import com.back.standard.util.page.PageUt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportValidator reportValidator;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final ReportQueryRepository reportQueryRepository;

    @Transactional
    public ReportResBody postReport(ReportReqBody reqBody, long reporterId) {
        reportValidator.validateTargetId(reqBody.reportType(), reqBody.targetId());

        Member reporter = memberRepository.findById(reporterId)
                                          .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        Report savedReport = reportRepository.save(Report.create(
                reqBody.targetId(), reqBody.comment(), reporter, reqBody.reportType()
        ));

        return ReportResBody.from(savedReport);
    }

    public PagePayload<ReportResBody> getReports(Pageable pageable, ReportType reportType) {
        Page<ReportResBody> pages = reportQueryRepository.getReports(pageable, reportType);
        return PageUt.of(pages);
    }

    @Transactional
    public void processAutoBan() {
        // 최근 30일 이내의 모든 신고 조회
        List<Report> recentReports = reportQueryRepository.findReportsWithinDays(30);

        if (recentReports.isEmpty()) {
            log.info("No reports found in last 30 days");
            return;
        }

        // targetId + ReportType 별로 그룹핑
        Map<ReportType, Map<Long, List<Report>>> groupedReports = recentReports.stream()
                .collect(Collectors.groupingBy(
                        Report::getReportType,
                        Collectors.groupingBy(Report::getTargetId)
                ));

        int totalBanned = 0;
        int threshold = 5; // 신고 임계값

        // 각 타입별로 처리
        for (Map.Entry<ReportType, Map<Long, List<Report>>> typeEntry : groupedReports.entrySet()) {
            ReportType reportType = typeEntry.getKey();
            Map<Long, List<Report>> targetReports = typeEntry.getValue();

            // threshold 이상인 대상 ID만 필터링
            List<Long> targetIdsToBan = targetReports.entrySet().stream()
                    .filter(entry -> entry.getValue().size() >= threshold)
                    .map(Map.Entry::getKey)
                    .toList();

            if (targetIdsToBan.isEmpty()) {
                continue;
            }

            // 타입별로 일괄 조회 및 처리 (N+1 방지)
            int bannedCount = switch (reportType) {
                case POST -> banPosts(targetIdsToBan, targetReports);
                case MEMBER -> banMembers(targetIdsToBan, targetReports);
                case REVIEW -> banReviews(targetIdsToBan, targetReports);
            };

            totalBanned += bannedCount;
        }

        log.info("Total {} targets banned", totalBanned);
    }

    private int banPosts(List<Long> postIds, Map<Long, List<Report>> targetReports) {
        // IN 쿼리로 일괄 조회 (N+1 방지)
        List<Post> posts = postRepository.findAllById(postIds);

        int bannedCount = 0;
        for (Post post : posts) {
            if (!post.getIsBanned()) {
                post.ban();
                bannedCount++;

                List<Report> reports = targetReports.get(post.getId());
                log.info("POST banned - ID: {}, Report count: {}",
                        post.getId(), reports.size());
            }
        }

        // 일괄 저장
//        if (bannedCount > 0) {
//            postRepository.saveAll(posts);
//        }

        return bannedCount;
    }

    private int banMembers(List<Long> memberIds, Map<Long, List<Report>> targetReports) {
        // IN 쿼리로 일괄 조회
        List<Member> members = memberRepository.findAllById(memberIds);

        int bannedCount = 0;
        for (Member member : members) {
            if (!member.isBanned()) {
                member.ban();
                bannedCount++;

                List<Report> reports = targetReports.get(member.getId());
                log.info("MEMBER banned - ID: {}, Report count: {}",
                        member.getId(), reports.size());
            }
        }

//        if (bannedCount > 0) {
//            memberRepository.saveAll(members);
//        }

        return bannedCount;
    }

    private int banReviews(List<Long> reviewIds, Map<Long, List<Report>> targetReports) {
        // IN 쿼리로 일괄 조회
        List<Review> reviews = reviewRepository.findAllById(reviewIds);

        int bannedCount = 0;
        for (Review review : reviews) {
            if (!review.isBanned()) {
                review.ban();
                bannedCount++;

                List<Report> reports = targetReports.get(review.getId());
                log.info("REVIEW banned - ID: {}, Report count: {}",
                        review.getId(), reports.size());
            }
        }

//        if (bannedCount > 0) {
//            reviewRepository.saveAll(reviews);
//        }

        return bannedCount;
    }
}
