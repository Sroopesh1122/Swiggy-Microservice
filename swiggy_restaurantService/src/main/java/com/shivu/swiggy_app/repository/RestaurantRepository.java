package com.shivu.swiggy_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	
	public Optional<Restaurant>  findByEmail(String email);
	
	@Query("SELECT r FROM Restaurant r ORDER BY r.rating desc LIMIT 5")
	public List<Restaurant> topFiveRestaurant();
	
	public Optional<Restaurant> findByPasswordResetToken(String token);

}
