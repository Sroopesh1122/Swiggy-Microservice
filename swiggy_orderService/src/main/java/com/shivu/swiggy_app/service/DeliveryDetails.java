package com.shivu.swiggy_app.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shivu.swiggy_app.request.DeliveryPartner;


public class DeliveryDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private DeliveryPartner deliveryPartner;
	
	
	public DeliveryDetails(DeliveryPartner deliveryPartner)
	{
		this.deliveryPartner = deliveryPartner;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of(new SimpleGrantedAuthority("ROLE_DELIVERY"));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
    public DeliveryPartner getUser()
    {
    	return deliveryPartner;
    }
	
	

}
