package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.User;

@FeignClient(name = "swiggy-userService")
public interface UserServiceClient 
{
	@GetMapping("/api/user/email")
	public User getUserByEmail(@RequestParam String email);
	
	@GetMapping("/api/user/userId/{id}")
	public User getUserById(@PathVariable Integer id);

}
