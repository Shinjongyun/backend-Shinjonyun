package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.RedisKey;
import com.company.demo.giftcoupon.event.CouponRequestEvent;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCouponWriter implements ItemWriter<CouponRequestEvent> {

    private final CustomKafkaProducer kafkaProducer;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void write(Chunk<? extends CouponRequestEvent> items) {
        for (CouponRequestEvent event : items) {
            kafkaProducer.sendRequestMessage(event);
        }

        // Kafka 메시지 전송이 끝난 뒤 누적 발급 수 증가
        // 즉, 쿠폰 발행했다라는 것을 Kafka 메시지 발행을 기준으로 함
        redisTemplate.opsForValue().increment(RedisKey.COUPON_REQUEST_COUNT_KEY, items.size());
    }
}
