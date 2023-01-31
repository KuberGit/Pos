package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "daily_sales")
public class DailySalesPojo {

    @Id
    private int id;
    @Column(nullable = false)
    private Date date; // todo remove from primary key and make one more primary key column --> done
    @Column(nullable = false)
    private int orders;
    @Column(nullable = false)
    private int items;
    @Column(nullable = false)
    private double revenue;
}
