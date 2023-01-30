package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Setter
@Getter
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"orderId","productId"})})
public class OrderItemPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int productId;
    private int orderId;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private double sellingPrice; // todo use snake casing

}
