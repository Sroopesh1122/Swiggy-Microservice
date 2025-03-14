package com.shivu.swiggy_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.Deliveries;

@Repository
public interface DeliveriesRepository extends JpaRepository<Deliveries, Integer> {

	@Query("SELECT d FROM Deliveries d WHERE d.deliveryPartnerId=:id AND (:q IS NULL OR :q='' OR d.deliveryStatus =:q )")
	public Page<Deliveries> findAllDeliveriesByPartnerId(@Param("id") Integer partnerId , @Param("q") String q , Pageable pageable);
	
	@Query("SELECT d FROM Deliveries d WHERE d.orderId=:id")
	public Deliveries findDeliveryByOrderId(@Param("id") Integer orderId);
}
