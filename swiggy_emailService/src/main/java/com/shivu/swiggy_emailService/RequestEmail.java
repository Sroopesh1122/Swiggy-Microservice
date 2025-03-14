package com.shivu.swiggy_emailService;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RequestEmail
{
	 private String email;
	 private String otp;
	 private LocalDateTime requestedAt;
}
