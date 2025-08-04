package com.demo.inventoryservice.producer;

import com.demo.inventoryservice.dto.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryProducer {
    private static final String TOPIC = "inventory-events";

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public void sendInventoryEvent(InventoryEvent inventoryEvent) {
        log.info("Sending inventory event: orderId={}, status={}",
                inventoryEvent.getOrderId(), inventoryEvent.getStatus());

        ListenableFuture<SendResult<String, InventoryEvent>> future =
                kafkaTemplate.send(TOPIC, inventoryEvent.getOrderId().toString(), inventoryEvent);

        future.addCallback(new ListenableFutureCallback<SendResult<String, InventoryEvent>>() {
            @Override
            public void onSuccess(SendResult<String, InventoryEvent> result) {
                log.info("Inventory event sent successfully: orderId={}, offset={}",
                        inventoryEvent.getOrderId(), result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable exception) {
                log.error("Failed to send inventory event: orderId={}",
                        inventoryEvent.getOrderId(), exception);
            }
        });
    }
}