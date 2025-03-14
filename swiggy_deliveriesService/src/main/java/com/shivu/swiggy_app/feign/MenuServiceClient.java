package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shivu.swiggy_app.request.MenuItem;

@FeignClient(name = "swiggy-menuItemService")
public interface MenuServiceClient {

	@GetMapping("/api/menu/item/{id}")
	public MenuItem getById(@PathVariable Integer id);
}
