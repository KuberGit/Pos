package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class OrderData {
    public int id;
    public String datetime;
    public double billAmount;
    public int isInvoiceCreated;
}