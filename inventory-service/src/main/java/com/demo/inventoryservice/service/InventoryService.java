package com.demo.inventoryservice.service;

import com.demo.inventoryservice.dto.InventoryEvent;
import com.demo.inventoryservice.dto.OrderEvent;
import com.demo.inventoryservice.entity.Inventory;
import com.demo.inventoryservice.producer.InventoryProducer;
import com.demo.inventoryservice.repository.InventoryRepository;
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
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryProducer inventoryProducer;

    public void reserveInventory(OrderEvent orderEvent) {
        log.info("Attempting to reserve inventory: product={}, quantity={}",
                orderEvent.getProductName(), orderEvent.getQuantity());

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductName(orderEvent.getProductName());

        if (inventoryOpt.isEmpty()) {
            log.warn("Product not found: {}", orderEvent.getProductName());
            sendInventoryEvent(orderEvent, "RESERVATION_FAILED", "Product not found");
            return;
        }

        Inventory inventory = inventoryOpt.get();

        try {
            if (inventory.canReserve(orderEvent.getQuantity())) {
                inventory.reserveQuantity(orderEvent.getQuantity());
                inventoryRepository.save(inventory);

                log.info("Inventory reserved successfully: product={}, quantity={}, available={}",
                        orderEvent.getProductName(), orderEvent.getQuantity(),
                        inventory.getAvailableQuantity());

                sendInventoryEvent(orderEvent, "RESERVED", "Inventory reserved successfully");
            } else {
                log.warn("Insufficient inventory: product={}, requested={}, available={}",
                        orderEvent.getProductName(), orderEvent.getQuantity(),
                        inventory.getAvailableQuantity());

                sendInventoryEvent(orderEvent, "RESERVATION_FAILED", "Insufficient inventory");
            }
        } catch (Exception e) {
            log.error("Error reserving inventory: product={}", orderEvent.getProductName(), e);
            sendInventoryEvent(orderEvent, "RESERVATION_FAILED", e.getMessage());
        }
    }

    public void handleOrderStatusUpdate(OrderEvent orderEvent) {
        log.info("Handling order status update: orderId={}, status={}",
                orderEvent.getOrderId(), orderEvent.getStatus());

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductName(orderEvent.getProductName());

        if (inventoryOpt.isEmpty()) {
            log.warn("Product not found for status update: {}", orderEvent.getProductName());
            return;
        }

        Inventory inventory = inventoryOpt.get();

        switch (orderEvent.getStatus()) {
            case "CONFIRMED" -> {
                inventory.confirmReservation(orderEvent.getQuantity());
                inventoryRepository.save(inventory);
                sendInventoryEvent(orderEvent, "CONFIRMED", "Inventory confirmed");
                log.info("Inventory confirmed: product={}, quantity={}",
                        orderEvent.getProductName(), orderEvent.getQuantity());
            }
            case "CANCELLED" -> {
                inventory.releaseReservation(orderEvent.getQuantity());
                inventoryRepository.save(inventory);
                sendInventoryEvent(orderEvent, "RELEASED", "Inventory released");
                log.info("Inventory released: product={}, quantity={}",
                        orderEvent.getProductName(), orderEvent.getQuantity());
            }
        }
    }

    private void sendInventoryEvent(OrderEvent orderEvent, String status, String reason) {
        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .orderId(orderEvent.getOrderId())
                .productName(orderEvent.getProductName())
                .quantity(orderEvent.getQuantity())
                .status(status)
                .eventType("INVENTORY_" + status)
                .reason(reason)
                .build();

        inventoryProducer.sendInventoryEvent(inventoryEvent);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryByProductName(String productName) {
        return inventoryRepository.findByProductName(productName);
    }
}