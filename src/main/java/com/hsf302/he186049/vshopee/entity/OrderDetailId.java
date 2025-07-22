package com.hsf302.he186049.vshopee.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderDetailId implements Serializable {

    private Integer productId;
    private Integer orderId;

    @Override
    public int hashCode() {
        return Objects.hash(productId, orderId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrderDetailId)) return false;
        OrderDetailId other = (OrderDetailId) obj;
        return Objects.equals(productId, other.productId) && Objects.equals(orderId, other.orderId);
    }
}
