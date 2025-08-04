package com.demo.inventoryservice.consumer;

import com.demo.inventoryservice.dto.OrderEvent;
import com.demo.inventoryservice.service.InventoryService;
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

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void consumeOrderEvent(
            @Payload OrderEvent orderEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Received order event: orderId={}, eventType={}, product={}, quantity={}",
                orderEvent.getOrderId(), orderEvent.getEventType(),
                orderEvent.getProductName(), orderEvent.getQuantity());

        try {
            switch (orderEvent.getEventType()) {
                case "ORDER_CREATED":
                    inventoryService.reserveInventory(orderEvent);
                    break;
                case "ORDER_STATUS_UPDATED":
                    inventoryService.handleOrderStatusUpdate(orderEvent);
                    break;
                default:
                    log.info("Unhandled event type: {}", orderEvent.getEventType());
                    break;
            }

            acknowledgment.acknowledge();
            log.info("Successfully processed inventory event: orderId={}", orderEvent.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process inventory event: orderId={}", orderEvent.getOrderId(), e);
            // In production, implement retry logic or dead letter queue
        }
    }
}