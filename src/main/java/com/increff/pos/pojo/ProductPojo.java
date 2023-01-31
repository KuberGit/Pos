package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "products",
        indexes = {
                @Index(name = "barcode_idx", columnList = "barcode", unique = true)
        })
public class ProductPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String barcode;
    @Column(name = "brand_category_id", nullable = false)
    private int brandCategoryId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double mrp;
}
