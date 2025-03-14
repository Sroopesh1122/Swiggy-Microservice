package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.shivu.swiggy_app.exception.MenuItemException;
import com.shivu.swiggy_app.feign.RestaurantServiceClient;
import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.User;
import feign.FeignException.FeignClientException;




@Service
public class CustomerUserDetailsService implements UserDetailsService{

	
	@Autowired
	private RestaurantServiceClient restaurantServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
	
	public RestaurantDetails loadRestaurantUser(String email) throws UsernameNotFoundException{
		
		Restaurant restaurant = null;	
		try {
			restaurant = restaurantServiceClient.getUserByEmail(email);
		} catch (FeignClientException e) {
			throw new MenuItemException("Unauthenticated");
		}	
		return new RestaurantDetails(restaurant);
		
	}
	
    public CustomerDetails loadCustomer(String email) {
		
		User user = userServiceClient.getUserByEmail(email);
		if(user == null)
		{
			throw new UsernameNotFoundException("User Not Found");
		}
		return new  CustomerDetails(user);
	}
	


}
