package com.demo.orderservice.service;

import com.demo.orderservice.dto.OrderEvent;
import com.demo.orderservice.entity.Order;
import com.demo.orderservice.entity.OrderStatus;
import com.demo.orderservice.producer.OrderProducer;
import com.demo.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order createOrder(Order order) {
        log.info("Creating new order for customer: {}", order.getCustomerName());

        Order savedOrder = orderRepository.save(order);

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .customerName(savedOrder.getCustomerName())
                .productName(savedOrder.getProductName())
                .quantity(savedOrder.getQuantity())
                .price(savedOrder.getPrice())
                .status(savedOrder.getStatus())
                .eventType("ORDER_CREATED")
                .build();

        orderProducer.sendOrderEvent(orderEvent);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status: orderId = {}, status = {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(updatedOrder.getId())
                .customerName(updatedOrder.getCustomerName())
                .productName(updatedOrder.getProductName())
                .quantity(updatedOrder.getQuantity())
                .price(updatedOrder.getPrice())
                .status(updatedOrder.getStatus())
                .eventType("ORDER_STATUS_UPDATED")
                .build();

        orderProducer.sendOrderEvent(orderEvent);

        return updatedOrder;
    }
}
