package com.demo.notificationservice.service;

import com.demo.notificationservice.dto.OrderEvent;
import com.demo.notificationservice.entity.Notification;
import com.demo.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public void processOrderEvent(OrderEvent orderEvent) {
        String message = generateNotificationMessage(orderEvent);

        // Create notification record
        Notification notification = Notification.builder()
                .orderId(orderEvent.getOrderId())
                .customerName(orderEvent.getCustomerName())
                .message(message)
                .channel("EMAIL")
                .build();

        try {
            // Send email notification
            emailService.sendOrderNotification(
                    orderEvent.getCustomerName() + "@example.com",
                    getEmailSubject(orderEvent.getEventType()),
                    message
            );

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());

            log.info("Notification sent successfully: orderId={}, customer={}",
                    orderEvent.getOrderId(), orderEvent.getCustomerName());

        } catch (Exception e) {
            notification.setStatus("FAILED");
            log.error("Failed to send notification: orderId={}, customer={}",
                    orderEvent.getOrderId(), orderEvent.getCustomerName(), e);
        }

        notificationRepository.save(notification);
    }

    private String generateNotificationMessage(OrderEvent orderEvent) {
        switch (orderEvent.getEventType()) {
            case "ORDER_CREATED":
                return String.format(
                        "Dear %s, your order #%d for %s (Quantity: %d) has been created successfully. Total: $%.2f",
                        orderEvent.getCustomerName(), orderEvent.getOrderId(),
                        orderEvent.getProductName(), orderEvent.getQuantity(), orderEvent.getPrice()
                );
            case "ORDER_STATUS_UPDATED":
                return String.format(
                        "Dear %s, your order #%d status has been updated to: %s",
                        orderEvent.getCustomerName(), orderEvent.getOrderId(), orderEvent.getStatus()
                );
            default:
                return String.format(
                        "Dear %s, there's an update on your order #%d",
                        orderEvent.getCustomerName(), orderEvent.getOrderId()
                );
        }
    }

    private String getEmailSubject(String eventType) {
        switch (eventType) {
            case "ORDER_CREATED":
                return "Order Confirmation";
            case "ORDER_STATUS_UPDATED":
                return "Order Status Update";
            default:
                return "Order Update";
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
}