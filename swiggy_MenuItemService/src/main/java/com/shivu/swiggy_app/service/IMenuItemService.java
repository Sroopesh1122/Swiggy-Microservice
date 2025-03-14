package com.shivu.swiggy_app.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shivu.swiggy_app.entity.MenuItem;
import com.shivu.swiggy_app.response.SuggestionResponse;


public interface IMenuItemService
{
 public MenuItem create(MenuItem menuItem);
 public MenuItem update(MenuItem menuItem);
 public MenuItem findById(Integer menuId);
 public List<MenuItem> findAll();
 
 public Page<MenuItem> findByRestaurant(Integer restaurantId ,String filter ,Integer page,Integer limit);
 
 public Page<MenuItem> findMenuItms(String filter ,Integer page,Integer limit,Integer rId);
 
 public Page<MenuItem> findMenuItems(String filter ,Integer page,Integer rid,Integer limit ,Integer minPrice,Integer maxPrice , Integer rating);
 
 public Set<SuggestionResponse> getSuggestionData(String filter );
 
 public Page<MenuItem> findSimilarItems (String regex , Pageable pageable);
 
 

 
}
