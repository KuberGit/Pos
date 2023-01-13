package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderForm {
	public String name;
	public String barcode;
	public int quantity;
	public double mrp;
}