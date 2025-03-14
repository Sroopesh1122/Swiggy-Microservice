package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DeliveryPartner {

	private Integer partnerId;

	private String name;

	private String phoneNumber;

	private String vehicleDetails;

	private LocalDateTime createdAt;

	private String email;

	private String password;

	private String passwordResetToken;

	private LocalDateTime passwordExpiredBy;
}
