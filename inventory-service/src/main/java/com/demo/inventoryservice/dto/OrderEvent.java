package com.demo.inventoryservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String eventType;
}