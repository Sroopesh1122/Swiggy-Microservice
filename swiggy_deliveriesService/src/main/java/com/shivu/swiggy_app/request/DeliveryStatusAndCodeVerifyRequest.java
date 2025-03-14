package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class DeliveryStatusAndCodeVerifyRequest {
	private Integer DeliveryId;
	private String deliveryCode;

}
