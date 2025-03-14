package com.shivu.swiggy_app.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.shivu.swiggy_app.request.Restaurant;

@FeignClient(name = "swiggy-restaurantService")
public interface RestaurantServiceClient 
{
	
	@GetMapping("/api/restaurant/restaurantId/{id}")
	public Restaurant getRestaurantById(@PathVariable("id") Integer rId);
	
	@GetMapping("/api/restaurant/email")
	public Restaurant getUserByEmail(@RequestParam String email);
	
	
	@PutMapping("/api/restaurant/rating/update")
	public Restaurant updateResturant(@RequestBody Restaurant restaurant);
	
	
	
}
