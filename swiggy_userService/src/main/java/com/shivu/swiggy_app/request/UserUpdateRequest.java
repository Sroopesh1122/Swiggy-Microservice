package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class UserUpdateRequest 
{
	private int userId;

	private String name;

	private String email;

	private String phoneNumber;

	private String address;

	private String password;
}
