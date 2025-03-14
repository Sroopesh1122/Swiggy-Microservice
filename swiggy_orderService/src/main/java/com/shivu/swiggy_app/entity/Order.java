package com.shivu.swiggy_app.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order
{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer orderId;
	
	private Double totalAmount;
	
	private String status;
	
	private String payMode;
	
	private String deliveryAddress;
	
	private Integer reviewed;
	
	private String razorpayId;
	
	
	private LocalDateTime createdAt;
	
	private Integer userId;
	
	private Integer restaurantId;
	
	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItems;
	
	
	private Integer pickedBy;
}
