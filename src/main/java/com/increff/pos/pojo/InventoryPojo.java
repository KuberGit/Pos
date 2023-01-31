package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "inventory" , uniqueConstraints={@UniqueConstraint(columnNames={"product_id"})})
public class InventoryPojo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Column(nullable = false)
    private int quantity;
}
