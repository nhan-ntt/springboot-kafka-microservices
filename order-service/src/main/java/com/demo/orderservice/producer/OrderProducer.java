package com.demo.orderservice.producer;

import com.demo.orderservice.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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

        ListenableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(TOPIC, orderEvent.getOrderId().toString(), orderEvent);

        future.addCallback(new ListenableFutureCallback<SendResult<String, OrderEvent>>() {
            @Override
            public void onSuccess(SendResult<String, OrderEvent> result) {
                log.info("order event sent successfully: orderId = {}, offset = {}",
                        orderEvent.getOrderId(), result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable exception) {
                log.error("Failed to send order event: orderId = {}",
                        orderEvent.getOrderId(), exception);
            }
        });
    }
}