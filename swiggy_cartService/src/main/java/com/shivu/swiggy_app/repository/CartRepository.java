package com.shivu.swiggy_app.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

	@Query("SELECT c from Cart c where c.userId=:id")
	public List<Cart> findCartsByUserId(@Param("id") Integer userId);
	
	@Query("SELECT COUNT(c) from Cart c where c.userId=:id")
	public Integer getCartsCountByUserId(@Param("id") Integer userId);
	
	//To check wheather the item is saved in cart or not
	@Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.userId=:uid AND c.menuItemId=:itemId")
    public boolean existsByUserIdAndItemId(@Param("uid") Long userId, @Param("itemId") Long itemId);
	
	
	@Modifying
	@Query("DELETE FROM Cart c WHERE c.userId=:id")
	public int deleteByCartItemByUserId(@Param("id") Integer userId);

}
