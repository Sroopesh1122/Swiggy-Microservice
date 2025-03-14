package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.entity.Order;
import com.shivu.swiggy_app.repository.OrderRepository;


@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public Order createOrder(Order order) {
		// TODO Auto-generated method stub
		return  orderRepository.save(order);
	}

	@Override
	public Order updateOrder(Order order) {
		// TODO Auto-generated method stub
		return  orderRepository.save(order);
	}

	@Override
	public Order getOrderById(Integer id) {
		return orderRepository.findById(id).get();
	}
	
	@Override
	public Page<Order> getAllOrdersByUserId(Integer userId, Pageable pageable) {
		// TODO Auto-generated method stub
		return  orderRepository.getAllOrderByUserid(userId,pageable) ;
	}
	
	@Override
	public Page<Order> getAllOrdersByRestaurant(Integer rId,String filter, Pageable pageable) {
		
		return orderRepository.getAllOrderByRestaurantId(rId,filter, pageable);
	}
	
	@Override
	public Page<Order> getAllUndevlieredOrders(String a, Pageable pageable) {
		// TODO Auto-generated method stub
		return orderRepository.getAllOrdersNotDelivered(a, pageable);
	}

}
