package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "swiggy-cartService")
public interface CartServiceClient 
{
	@DeleteMapping("/api/cart/cartId/{id}")
    public void deleteCartItemsByUserId(@PathVariable Integer id);
	
	@GetMapping("/api/cart/isSaved")
	public Boolean isSaved(@RequestParam Integer userId , @RequestParam Integer menuId);
}
