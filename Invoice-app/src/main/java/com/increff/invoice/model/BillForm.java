package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BillForm {
    public String name;
    public int quantity;
    public double mrp;
}