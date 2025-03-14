package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivu.swiggy_app.entity.Restaurant;



@Service
public class CustomerUserDetailsService implements UserDetailsService{

	
	@Autowired
	private IRestaurantService restaurantService;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
	
	@Transactional
	public RestaurantDetails loadRestaurantUser(String email) throws UsernameNotFoundException{
		
		Restaurant restaurant = restaurantService.findByEmail(email);
		if(restaurant ==null)
		{
			throw new UsernameNotFoundException("User Not Found");
		}
		return new RestaurantDetails(restaurant);
		
	}
	


}
