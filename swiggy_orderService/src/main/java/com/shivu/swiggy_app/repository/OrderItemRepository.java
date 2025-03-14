package com.shivu.swiggy_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.OrderItem;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	@Query("SELECT o FROM OrderItem o WHERE o.order.orderId=:id")
	List<OrderItem> findByOrderId(@Param("id") Integer orderId);
	
}
