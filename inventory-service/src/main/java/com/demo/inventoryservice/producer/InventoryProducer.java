package com.demo.inventoryservice.producer;

import com.demo.inventoryservice.dto.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryProducer {
    private static final String TOPIC = "inventory-events";

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public void sendInventoryEvent(InventoryEvent inventoryEvent) {
        log.info("Sending inventory event: orderId={}, status={}",
                inventoryEvent.getOrderId(), inventoryEvent.getStatus());

        CompletableFuture<SendResult<String, InventoryEvent>> future =
                kafkaTemplate.send(TOPIC, inventoryEvent.getOrderId().toString(), inventoryEvent);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Inventory event sent successfully: orderId={}, offset={}",
                        inventoryEvent.getOrderId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send inventory event: orderId={}",
                        inventoryEvent.getOrderId(), exception);
            }
        });
    }
}