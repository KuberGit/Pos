package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrderData {
    private Integer Id;
    private Timestamp createdAt;
    private Double billAmount;
    private Boolean isInvoiceCreated;
}
