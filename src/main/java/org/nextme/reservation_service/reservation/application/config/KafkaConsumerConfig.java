package org.nextme.reservation_service.reservation.application.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, PaymentConfirmedEvent> paymentConfirmedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // 기본 Kafka 설정만 직접 지정
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "34.22.87.75:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reservation-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // JsonDeserializer 관련 프로퍼티는 props에 절대 넣지 말기

        JsonDeserializer<PaymentConfirmedEvent> valueDeserializer =
                new JsonDeserializer<>(PaymentConfirmedEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmedEvent>
    orderCreatedKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentConfirmedEvent> orderCreatedConsumerFactory
    ) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, ex) ->
                        log.error("[OrderCreatedEventListener] 처리 실패, 메시지 스킵. record={}", record, ex),
                new FixedBackOff(0L, 0L) // 재시도 0회
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
