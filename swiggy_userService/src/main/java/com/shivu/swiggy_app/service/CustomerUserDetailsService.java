package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivu.swiggy_app.entity.User;
import com.shivu.swiggy_app.repository.UserRepository;



@Service
public class CustomerUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	
	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
	
	public CustomerDetails loadCustomer(String email) {
		
		User user = userRepository.findByEmail(email).orElse(null);
		if(user == null)
		{
			throw new UsernameNotFoundException("User Not Found");
		}
		return new  CustomerDetails(user);
	}
}
