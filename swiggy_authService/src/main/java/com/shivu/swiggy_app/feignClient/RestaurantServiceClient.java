package com.shivu.swiggy_app.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.RestaurantSignUpRequest;

@FeignClient(name = "swiggy-restaurantService")
public interface RestaurantServiceClient 
{
	@PostMapping("/api/restaurant/forgotPassword")
	public Map<String, Object> forgotPassword(@RequestBody ForgotPasswordRequest request);
	
	@PostMapping("/api/restaurant/resetPassword")
	public Map<String, Object> resetPassword(@RequestBody PasswordResetRequest request);
	
	@GetMapping("/api/restaurant/{id}")
	public Restaurant getRestaurantById(@PathVariable("id") Integer rId);
	
	@GetMapping("/api/restaurant/email")
	public Restaurant getUserByEmail(@RequestParam String email);
	
	@PostMapping("/api/restaurant/create")
	public Restaurant createUser(@RequestBody RestaurantSignUpRequest request);
}
