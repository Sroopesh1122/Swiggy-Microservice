package com.shivu.swiggy_app.request;

import java.util.List;

import lombok.Data;

@Data
public class PlaceOrderRequest 
{
    private Integer userId;
	private String mode;
	private String deliveryAddress;
	private List<PlacedOrderItem> items;
	private Integer totalAmount;
	private Integer checkoutAmount;
	private String razorPayId;
	private String source;
	
}





