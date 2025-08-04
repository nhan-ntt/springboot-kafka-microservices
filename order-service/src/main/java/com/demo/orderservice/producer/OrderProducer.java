package com.demo.orderservice.producer;

import com.demo.orderservice.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    // Sends an order event to the Kafka topic
    public void sendOrderEvent(OrderEvent orderEvent) {
        log.info("Sending order event: {}", orderEvent.getOrderId());

        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(TOPIC, orderEvent.getOrderId().toString(), orderEvent);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("order event sent successfully: orderId = {}, offset = {}",
                        orderEvent.getOrderId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send order event: orderId = {}",
                        orderEvent.getOrderId(), exception);
            }
        });
    }
}
