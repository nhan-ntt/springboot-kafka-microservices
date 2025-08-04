package com.demo.orderservice.dto;

import com.demo.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private OrderStatus status;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String eventType; // ISO 8601 format for date-time
}
