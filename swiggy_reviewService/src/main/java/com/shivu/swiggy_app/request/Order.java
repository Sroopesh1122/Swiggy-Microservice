package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;


@Data
public class Order
{

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
	
	private List<OrderItem> orderItems;
	
	
	private Integer pickedBy;
}
