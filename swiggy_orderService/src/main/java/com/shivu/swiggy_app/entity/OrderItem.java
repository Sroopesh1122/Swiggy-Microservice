package com.shivu.swiggy_app.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem 
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer orderItemId;

	private Integer quantity;

	private Double price;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "orderId")
	private Order order;
	
	private Integer menuItemId;
}
