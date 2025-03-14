package com.shivu.swiggy_app.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.CreateDeliveryPartnerRequest;
import com.shivu.swiggy_app.request.DeliveryPartner;
import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;

@FeignClient(name = "swiggy-deliveryPartnerService")
public interface DeliveryPartnerServiceClient
{
	@PostMapping("/delivery/forgotPassword")
	public Map<String, Object> forgotPassword(@RequestBody ForgotPasswordRequest request);
	
	@PostMapping("/delivery/resetPassword")
	public Map<String, Object> resetPassword(@RequestBody PasswordResetRequest request);
	
	@PostMapping("/delivery/create")
	public DeliveryPartner createDeliveryPartner(@RequestBody CreateDeliveryPartnerRequest request);
	
	@GetMapping("/delivery/email")
	public DeliveryPartner getDeliveryPartnerByEmail(@RequestParam String email);
}
