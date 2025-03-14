package com.shivu.swiggy_app.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shivu.swiggy_app.request.MenuItem;

@FeignClient(name = "swiggy-menuItemService")
public interface MenuItemClient {

	
	@GetMapping("/api/menu/item/{id}")
	public MenuItem getMenuItemById(@PathVariable Integer id);
	
	@PutMapping("/api/menu/public/update")
	public MenuItem updateMenuItem(@RequestBody MenuItem menuItem);
	
	@PutMapping("/api/menu/public/rating/update")
	public MenuItem updateMenuItemRating(@RequestBody MenuItem menuItem);
	
}
