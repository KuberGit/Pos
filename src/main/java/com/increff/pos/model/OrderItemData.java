package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemData extends OrderItemForm {
    public int id;
    public int orderId;
}