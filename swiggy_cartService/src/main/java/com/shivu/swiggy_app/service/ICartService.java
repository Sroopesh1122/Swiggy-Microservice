package com.shivu.swiggy_app.service;

import java.util.List;

import com.shivu.swiggy_app.entity.Cart;



public interface ICartService 
{
  public Cart addToCart(Cart cart);
  
  public Cart getById(Integer cartId);
  
  public void deleteCartItem(Integer cartId);
  
  public List<Cart> allCartItemsByUserId(Integer userId );
  
  public boolean isSaved(Integer userId , Integer itemId);
  
  public Integer getCartItemCount (Integer userId); 
  
  public Integer deleteCartItemsByUserId(Integer userId);
  
}
