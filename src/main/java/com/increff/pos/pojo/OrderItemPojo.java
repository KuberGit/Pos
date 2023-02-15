package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Setter
@Getter
@Table(name = "order_items", uniqueConstraints={@UniqueConstraint(columnNames={"order_id","product_id"})})

public class OrderItemPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Column(name = "order_id", nullable = false)
    private int orderId;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "selling_price", nullable = false)
    private double sellingPrice;

}
