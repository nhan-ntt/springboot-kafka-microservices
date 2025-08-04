package com.demo.notificationservice.repository;

import com.demo.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderId(Long orderId);
    List<Notification> findByCustomerName(String customerName);
}
