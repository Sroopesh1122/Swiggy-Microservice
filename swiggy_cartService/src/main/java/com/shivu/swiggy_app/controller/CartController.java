package com.shivu.swiggy_app.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.Cart;
import com.shivu.swiggy_app.exception.CartException;
import com.shivu.swiggy_app.feign.MenuServiceClient;
import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.CartAddRequest;
import com.shivu.swiggy_app.service.CustomerDetails;
import com.shivu.swiggy_app.service.ICartService;
import com.shivu.swiggy_app.util.MessageReader;

import feign.FeignException;
import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	private final Integer CART_MAX_LIMIT = 25;

	@Autowired
	private ICartService cartService;

	@Autowired
	private MenuServiceClient menuServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;

	@PostMapping("/add")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> addToCart(
			@AuthenticationPrincipal CustomerDetails customerDetails,
			@RequestBody CartAddRequest request) {
		
		Map<String, String> response = new HashMap<>();
		
		if(customerDetails==null)
		{
			throw new UsernameNotFoundException("Unauthorized");
		}

		if (request.getMenuId() != null && request.getMenuId() != 0) {
			
			try {
				userServiceClient.getUserById(request.getUserId());
			} catch (FeignException e) {
				throw new CartException(MessageReader.getMessage(e));
			}
		
			try {
				menuServiceClient.getById(request.getMenuId());
			} catch (FeignException e) {
				throw new CartException(MessageReader.getMessage(e));
			}
			
			
			if (cartService.isSaved(request.getUserId(), request.getMenuId())) {
				throw new CartException("Already Saved");
			}
				
	
			if (cartService.getCartItemCount(request.getUserId()) >= CART_MAX_LIMIT) {
				throw new CartException("Reached Maximum Limit,Remove items from cart to add");
			}

			Cart cart = new Cart();
			cart.setUserId(request.getUserId());
            cart.setMenuItemId(request.getMenuId());
			cartService.addToCart(cart);
			response.put("status", "success");
			response.put("message", "Item added tomcart Successfully");

		}
		return ResponseEntity.ok(response);
	}

	@GetMapping("/")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> getAllCartByUser(
			@AuthenticationPrincipal CustomerDetails customerDetails , 
			@RequestParam Integer userId) {
		
		System.out.println(userId);
		
		if(customerDetails==null)
		{
			throw new UsernameNotFoundException("Unauthorized");
		}
		
		try {
			userServiceClient.getUserById(userId);
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new CartException("User Not Found");
		}
		
		List<Map<String , Object>> cartItems = cartService.allCartItemsByUserId(userId).stream().map(cart->{
			Map<String , Object> cartMap = new HashMap<>();
			cartMap.put("cartId", cart.getCartId());
			cartMap.put("menuItem", menuServiceClient.getById(cart.getMenuItemId()));
			cartMap.put("user",userServiceClient.getUserById(cart.getUserId()) );
			
			return cartMap;
		}).collect(Collectors.toList());
		
		return ResponseEntity.ok(cartItems);
	}

	@DeleteMapping("/")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> deleteItemFromCart(
			@AuthenticationPrincipal CustomerDetails customerDetails ,
			@RequestParam Integer cartId)
	{
		Map<String, String> response =  new HashMap<>();
		
		if(customerDetails==null)
		{
			throw new CartException("Unauthorized");
		}
		
		Cart cart = cartService.getById(cartId);
		if(cart == null)
		{
			throw new CartException("Cart Item Not Found");
		}
		
		cartService.deleteCartItem(cartId);
		response.put("status", "success");
		response.put("message","Item removed from cart successfully");
		return ResponseEntity.ok(response);
	}
	
	
	
	@GetMapping("/isSaved")
	public ResponseEntity<?> isSaved(@RequestParam Integer userId , @RequestParam Integer menuId)
	{
	  return ResponseEntity.ok(cartService.isSaved(userId, menuId));
	}
	
    @DeleteMapping("/cartId/{id}")
    public void deleteCartItemsByUserId(@PathVariable Integer id)
    {    	
    	cartService.deleteCartItemsByUserId(id);
    	return ;
    	
    }
	
	

}
