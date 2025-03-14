package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest 
{
	private Integer orderId;
	private String status;
}
