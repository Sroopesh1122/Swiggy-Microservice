package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {

	private int userId;

	private String name;

	private String email;

	private String phoneNumber;

	private String address;

	private String password;

	private LocalDateTime createdAt;
	
	private String passwordResetToken;
	
	private LocalDateTime passwordExpiredBy;
	
}
