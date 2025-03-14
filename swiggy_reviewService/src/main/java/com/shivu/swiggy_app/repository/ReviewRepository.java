package com.shivu.swiggy_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> 
{
	@Query("select r from Review r where r.menuItemId=:itemId")
	Page<Review> getAllReviewsByItemId(@Param("itemId") Integer menuId ,Pageable pageable);
	  
	@Query("select r from Review r where r.restaurantId=:rId")
	Page<Review> getAllReviewsByRestaurantId(@Param("rId") Integer menuId ,Pageable pageable);
}