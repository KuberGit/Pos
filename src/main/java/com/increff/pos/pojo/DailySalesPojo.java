package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class DailySalesPojo {
    @Id
    private Date date;
    private int orders;
    private int items;
    private double revenue;
}
