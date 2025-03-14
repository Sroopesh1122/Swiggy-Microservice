package com.shivu.swiggy_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.entity.Cart;
import com.shivu.swiggy_app.repository.CartRepository;

import jakarta.transaction.Transactional;


@Service
public class CartServiceImpl implements ICartService {

	
	@Autowired
	private CartRepository cartRepository;
	
	@Override
	public Cart addToCart(Cart cart) {
		
		return cartRepository.save(cart);
	}

	@Override
	public void deleteCartItem(Integer cartId) {
	
		 cartRepository.deleteById(cartId);

	}

	@Override
	public List<Cart> allCartItemsByUserId(Integer userId) {
		return cartRepository.findCartsByUserId(userId);
	}
	
	@Override
	public boolean isSaved(Integer userId, Integer itemId) {
		return cartRepository.existsByUserIdAndItemId(userId.longValue(),itemId.longValue());
	}
	
	@Override
	public Integer getCartItemCount(Integer userId) {
		return cartRepository.getCartsCountByUserId(userId);
	}
	
	@Transactional
	@Override
	public Integer deleteCartItemsByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return cartRepository.deleteByCartItemByUserId(userId);
	}
	
	@Override
	public Cart getById(Integer cartId) {
		
		return cartRepository.findById(cartId).orElse(null);
	}

}
