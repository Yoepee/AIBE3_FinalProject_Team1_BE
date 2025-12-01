package com.back.domain.reservation.scheduler;

import com.back.domain.reservation.scheduler.job.ReservationReturnRemindJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationRemindScheduler {

    private final Scheduler scheduler;

    @PostConstruct
    public void init() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(ReservationReturnRemindJob.class)
                    .withIdentity("returnReminderJob", "reservation")
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("returnReminderTrigger", "reservation")
                    .withSchedule(
                            CronScheduleBuilder.dailyAtHourAndMinute(10, 0)
                    )
                    .forJob(jobDetail)
                    .build();

            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("[SCHEDULER] 반납 리마인더 스케줄링 완료");
            }
        } catch (SchedulerException e) {
            log.error("[SCHEDULER] 반납 리마인더 스케줄링 실패", e);
        }
    }
}