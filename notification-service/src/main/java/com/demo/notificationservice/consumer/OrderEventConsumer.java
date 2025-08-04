package com.demo.notificationservice.consumer;

import com.demo.notificationservice.dto.OrderEvent;
import com.demo.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(
            @Payload OrderEvent orderEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Received order event: orderId={}, eventType={}, topic={}, partition={}, offset={}",
                orderEvent.getOrderId(), orderEvent.getEventType(), topic, partition, offset);

        try {
            notificationService.processOrderEvent(orderEvent);
            acknowledgment.acknowledge();
            log.info("Successfully processed order event: orderId={}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process order event: orderId={}", orderEvent.getOrderId(), e);
            // In a real application, you might want to implement retry logic or dead letter queue
        }
    }
}