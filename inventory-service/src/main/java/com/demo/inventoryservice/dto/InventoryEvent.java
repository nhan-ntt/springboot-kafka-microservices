package com.demo.inventoryservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {
    private Long orderId;
    private String productName;
    private Integer quantity;
    private String status; // RESERVED, CONFIRMED, RELEASED
    private String eventType;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String reason;

    public InventoryEvent(Long orderId, String productName, Integer quantity,
                          String status, String eventType) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
}