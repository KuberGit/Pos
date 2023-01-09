package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"productId"})})
public class InventoryPojo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(nullable = false)
    private int productId;
    @Column(nullable = false)
    private int quantity;
}
