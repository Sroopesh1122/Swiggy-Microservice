package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.exception.DeliveryException;
import com.shivu.swiggy_app.feign.DeliveryPartnerServiceClient;
import com.shivu.swiggy_app.feign.RestaurantServiceClient;
import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.DeliveryPartner;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.User;

import feign.FeignException;
import feign.FeignException.FeignClientException;




@Service
public class CustomerUserDetailsService implements UserDetailsService{

	
	@Autowired
	private RestaurantServiceClient restaurantServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	@Autowired
	private DeliveryPartnerServiceClient deliveryPartnerServiceClient;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
	
	public RestaurantDetails loadRestaurantUser(String email) throws UsernameNotFoundException{
		
		Restaurant restaurant = null;	
		try {
			restaurant = restaurantServiceClient.getUserByEmail(email);
		} catch (FeignClientException e) {
			throw new DeliveryException("Unauthenticated");
		}	
		return new RestaurantDetails(restaurant);
		
	}
	
    public CustomerDetails loadCustomer(String email) {
		
		User user = null;
		
		try {
			user = userServiceClient.getUserByEmail(email);
		} catch (FeignException e) {
			throw new DeliveryException("Unauthenticated");
		}
		
		return new  CustomerDetails(user);
	}
    
    public DeliveryDetails loadDelivery(String email) {
		
		DeliveryPartner deliveryPartner = null;
		
		try {
			deliveryPartner = deliveryPartnerServiceClient.getDeliveryPartnerByEmail(email);
		} catch (FeignException e) {
			// TODO: handle exception
			throw new DeliveryException("Unauthenticated");
		}
		
		
		
		return new  DeliveryDetails(deliveryPartner);
	}
    
    
    
	


}
