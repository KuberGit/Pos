package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemForm {
    public String barcode;
    public String name;
    public int quantity;
    public double sellingPrice;
}