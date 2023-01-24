package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchForm {

    private int id;
    private String orderUser;
    private String startDate;
    private String endDate;

}
