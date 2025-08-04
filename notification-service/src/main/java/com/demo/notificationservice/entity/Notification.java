package com.demo.notificationservice.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String customerName;
    private String message;
    private String channel; // EMAIL, SMS, PUSH
    @Builder.Default
    private String status = "PENDING"; // SENT, FAILED, PENDING
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime sentAt;

    public Notification(Long orderId, String customerName, String message, String channel) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.message = message;
        this.channel = channel;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}