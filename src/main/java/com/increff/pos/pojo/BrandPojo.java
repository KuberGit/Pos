package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "brand_categories",
        indexes = {
                @Index(name = "brand_category_idx", columnList = "brand,category", unique = true)
        })
public class BrandPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String brand; // todo check unique comb --> done and null or not --> done
    @Column(nullable = false)
    private String category;

}
