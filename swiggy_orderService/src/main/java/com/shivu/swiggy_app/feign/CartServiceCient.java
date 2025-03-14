package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "swiggy-cartService")
public interface CartServiceCient {

	
	@DeleteMapping("/api/cart/cartId/{id}")
    public void deleteCartItemsByUserId(@PathVariable Integer id);
	
}
