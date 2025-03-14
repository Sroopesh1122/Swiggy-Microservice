package com.shivu.swiggy_app.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignUpRequest 
{
	@NotBlank(message = "required")
	private String name;
	
	@Email(message = "Invalied Email")
	@NotBlank(message = "required")
	private String email;
	
	@NotBlank(message = "required")
	private String address;
	
	@NotBlank(message = "required")
	private String phoneNumber;
	
	@NotBlank(message = "required")
	private String password;
}
