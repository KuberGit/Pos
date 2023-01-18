package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter

public class OrderData {
    public int id;
    public Date datetime;
    public double billAmount;
    public int isInvoiceCreated;
}