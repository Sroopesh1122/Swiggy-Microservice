package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import lombok.Data;

@Data
public class UpdateDeliveryPartnerRequest {
	private Integer partnerId;
	private String name;
	private String phoneNumber;
	private String vehicleDetails;
	private LocalDateTime createdAt;
}
