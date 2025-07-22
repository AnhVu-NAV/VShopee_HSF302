package com.hsf302.he186049.vshopee.entity;

import com.hsf302.he186049.vshopee.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Transient // Không ánh xạ vào database
    public double getTotal() {
        if (order == null || order.getOrderDetails() == null) return 0;
        return order.getOrderDetails().stream()
                .mapToDouble(od -> od.getPrice() * od.getQuantity())
                .sum();
    }

}
