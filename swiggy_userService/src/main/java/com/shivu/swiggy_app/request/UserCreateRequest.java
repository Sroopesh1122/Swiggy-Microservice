package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserCreateRequest {

	private String name;

	private String email;

	private String phoneNumber;

	private String address;

	private String password;

	private LocalDateTime createdAt;
	
}
