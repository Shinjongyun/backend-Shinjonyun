package com.company.demo.common.config.batch;

import com.company.demo.giftcoupon.batch.CouponRequestProcessor;
import com.company.demo.giftcoupon.batch.KafkaCouponWriter;
import com.company.demo.giftcoupon.event.CouponRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import com.company.demo.giftcoupon.batch.RedisCouponReader;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CouponRequestJobConfig {
    private final RedisCouponReader redisCouponReader;
    private final CouponRequestProcessor couponRequestProcessor;
    private final KafkaCouponWriter kafkaCouponWriter;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job couponRequestJob() {
        return new JobBuilder("couponIssueJob", jobRepository)
                .start(couponRequestStep())
                .build();
    }

    @Bean
    public Step couponRequestStep() {
        return new StepBuilder("couponIssueStep", jobRepository)
                .<String, CouponRequestEvent>chunk(10, transactionManager)
                .reader(redisCouponReader)
                .processor(couponRequestProcessor)
                .writer(kafkaCouponWriter)
                .build();
    }
}
