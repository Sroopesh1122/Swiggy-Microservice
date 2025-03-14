package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.User;

import feign.FeignException.FeignClientException;




@Service
public class CustomerUserDetailsService implements UserDetailsService{

	
	@Autowired
	private UserServiceClient userServiceClient;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return null;
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
