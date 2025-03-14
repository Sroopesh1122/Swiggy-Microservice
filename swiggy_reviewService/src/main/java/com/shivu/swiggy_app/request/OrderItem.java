package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class OrderItem 
{
	private Integer orderItemId;

	private Integer quantity;

	private Double price;
	
	private Order orderId;
	
	private Integer menuItemId;
}
