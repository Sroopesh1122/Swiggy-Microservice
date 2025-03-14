package com.shivu.swiggy_app.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shivu.swiggy_app.entity.Order;


public interface IOrderService  {

	public Order createOrder(Order order);
	public Order updateOrder(Order order);
	public Order getOrderById(Integer id);
	public Page<Order> getAllOrdersByUserId(Integer userId, Pageable pageable);
	
	public Page<Order> getAllOrdersByRestaurant(Integer rId ,String filter, Pageable pageable);
	
	public Page<Order> getAllUndevlieredOrders(String a, Pageable pageable);
	
}
