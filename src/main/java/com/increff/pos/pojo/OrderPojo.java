package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "orders")
public class OrderPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name = "date_time", nullable = false)
	private Date datetime;
	@Column(name = "invoice_created", nullable = false)
	private int invoiceCreated;

}
