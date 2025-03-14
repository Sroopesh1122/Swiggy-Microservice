package com.shivu.swiggy_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

	
	@Query("SELECT o from Order o Where o.userId=:id")
	public Page<Order> getAllOrderByUserid(@Param("id") Integer userId ,Pageable pageable);
	
	@Query("SELECT o FROM Order o WHERE (o.status = '' OR o.status IS NULL OR o.status = :q) AND o.restaurantId= :id")
	Page<Order> getAllOrderByRestaurantId(@Param("id") Integer userId, @Param("q") String filter, Pageable pageable);
	
	
	@Query("SELECT o FROM Order o WHERE o.status = 'prepared' " +
		       "AND (o.pickedBy IS NULL) " +
		       "AND (:q IS NULL OR :q = '' OR o.deliveryAddress LIKE CONCAT(:q, '%'))")
		Page<Order> getAllOrdersNotDelivered(@Param("q") String filter, Pageable pageable);



	
}
