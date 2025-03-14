package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class RestaurantUpdateRequest 
{
    private int restaurantId;
	
	private String name;
	
	private String address;
	
	private String phoneNumber;
	
	private String password;
	
	private String email;
	
	private Double rating;
	
	private Integer reviewsCount;
	
    private String passwordResetToken;
	
	private LocalDateTime passwordExpiredBy;
}
