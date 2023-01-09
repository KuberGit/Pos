package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"barcode"})})
public class ProductPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String barcode;
    @Column(nullable = false)
    private int brandCategoryId;
    private String name;
    @Column(nullable = false)
    private Double mrp;
}
