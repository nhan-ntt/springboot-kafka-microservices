package com.demo.inventoryservice.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String productName;

    private Integer quantity;
    @Builder.Default
    private Integer reservedQuantity = 0;

    public Inventory(String productName, Integer quantity) {
        this.productName = productName;
        this.quantity = quantity;
        this.reservedQuantity = 0;
    }

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean canReserve(Integer requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    public void reserveQuantity(Integer quantityToReserve) {
        if (canReserve(quantityToReserve)) {
            this.reservedQuantity += quantityToReserve;
        } else {
            throw new IllegalArgumentException("Insufficient inventory");
        }
    }

    public void releaseReservation(Integer quantityToRelease) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantityToRelease);
        this.quantity = this.quantity + quantityToRelease;
    }

    public void confirmReservation(Integer quantityToConfirm) {
        if (this.reservedQuantity >= quantityToConfirm) {
            this.quantity -= quantityToConfirm;
            this.reservedQuantity -= quantityToConfirm;
        } else {
            throw new IllegalArgumentException("Cannot confirm more than reserved");
        }
    }
}