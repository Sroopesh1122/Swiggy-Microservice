package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.exception.ReviewException;
import com.shivu.swiggy_app.feignClient.RestaurantServiceClient;
import com.shivu.swiggy_app.feignClient.UserServiceClient;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.User;
import com.shivu.swiggy_app.util.MessageReader;

import feign.FeignException;




@Service
public class CustomerUserDetailsService implements UserDetailsService{

	
	@Autowired
	private UserServiceClient userServiceClient;
	
	
	@Autowired
	private RestaurantServiceClient restaurantServiceClient;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return null;
	}
	
	
	
    public CustomerDetails loadCustomer(String email) {
		
		User user =null;
		
		
		try {
			user = userServiceClient.getUserByEmail(email);
		} catch (FeignException e) {
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		return new  CustomerDetails(user);
	}
    
    
     public RestaurantDetails loadRestaurant(String email) {
		
		Restaurant restaurant =null;
		
		try {
			restaurant  = restaurantServiceClient.getUserByEmail(email);
		} catch (FeignException e) {
			// TODO: handle exception
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		return new RestaurantDetails(restaurant);
		
	}
	


}
