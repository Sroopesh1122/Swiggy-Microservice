package com.shivu.swiggy_app.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.User;

@FeignClient(name = "swiggy-userService")
public interface UserServiceClient 
{
	@GetMapping("/api/user/email")
	public User getUserByEmail(@RequestParam String email);
	
	@PostMapping("/api/user/create")
	public User createUser(@RequestBody User user);
	
	@PostMapping("/api/user/forgotPassword")
	public Map<String, Object> forgotPassword(@RequestBody ForgotPasswordRequest request);
	
	@PostMapping("/api/user/resetPassword")
	public Map<String, Object> resetPassword(@RequestBody PasswordResetRequest request);

}
