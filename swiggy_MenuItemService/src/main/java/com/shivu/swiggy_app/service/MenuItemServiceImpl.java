package com.shivu.swiggy_app.service;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivu.swiggy_app.entity.MenuItem;
import com.shivu.swiggy_app.exception.MenuItemException;
import com.shivu.swiggy_app.repository.MenuItemRepository;
import com.shivu.swiggy_app.response.SuggestionResponse;


@Service
public class MenuItemServiceImpl implements IMenuItemService {
	

	@Autowired
	private MenuItemRepository menuItemRepository;
	
	@Override
	public MenuItem create(MenuItem menuItem) {
		return menuItemRepository.save(menuItem);
	}

	@Override
	public MenuItem update(MenuItem menuItem) {
		return menuItemRepository.save(menuItem);
	}

	@Override
	public MenuItem findById(Integer menuId) {
		
		Optional<MenuItem> menOptional = menuItemRepository.findById(menuId);
		if(menOptional.isEmpty())
		{
			throw new MenuItemException("Menu Item Not Found");
		}
		
		return menOptional.get();
	}

	@Override
	public List<MenuItem> findAll() {
		return menuItemRepository.findAll();
	}

	@Override
	public Page<MenuItem> findByRestaurant(Integer restaurantId ,String filter ,Integer page ,Integer limit) {
		
		Pageable pageable =PageRequest.of(page, limit,Sort.by("itemId").descending());

		Page<MenuItem> menuItems = menuItemRepository.getMenuItems(filter, restaurantId, 0, 0, 0, pageable);
		
		return menuItems;
	}
	
	 @Override
	public Page<MenuItem> findMenuItms(String filter, Integer page, Integer limit ,Integer rId) {
		    Pageable pageable =PageRequest.of(page, limit);
			
			Page<MenuItem> menuItems = menuItemRepository.findMenuItems( filter,rId, pageable);
			
			return menuItems;
	}
	 
	 @Transactional
	 @Override
	public Set<SuggestionResponse> getSuggestionData(String filter ) {
		 Set<SuggestionResponse> suggestion = new HashSet<>();

		 if (filter == null || filter.isEmpty()) {
		     return suggestion;
		 }
		 
		
		 List<MenuItem> suggestionData = menuItemRepository.searchText(filter);

		 suggestion = suggestionData.stream()
		     .map(obj -> {
		        
		         if (obj.getName().toLowerCase().startsWith(filter.toLowerCase())) {
		             SuggestionResponse suggestionResponse = new SuggestionResponse();
		             suggestionResponse.setTitle(obj.getName());
		             suggestionResponse.setType("food");
		             return suggestionResponse;
		         } else if (obj.getCategory().contains("#" + filter)) {
		        	 
		        	 String[] terms = obj.getCategory().split(",");
		             return Arrays.stream(terms)
		                 .filter(term -> term.toLowerCase().startsWith("#" + filter.toLowerCase()))
		                 .findFirst()
		                 .map(term -> {
		                	 SuggestionResponse suggestionResponse = new SuggestionResponse();
		                	 suggestionResponse.setTitle(term.replace("#", ""));
		                	 suggestionResponse.setType("category");
		                	 return suggestionResponse;
		                 })
		                 .orElse(null);
		         } 
		         return null;
		     })
		     .filter(Objects::nonNull)
		     .collect(Collectors.toSet()); 

		 return suggestion;

	}

	 
	 
	 @Override
	public Page<MenuItem> findMenuItems(String filter, Integer page,Integer rid ,Integer limit,Integer minPrice,Integer maxPrice , Integer rating) {
		 		 
		 Pageable pageable = PageRequest.of(page, limit, Sort.by("rating").descending());
		 
		 return menuItemRepository.getMenuItems(filter,rid, minPrice,maxPrice,rating,pageable );
		 
	}
	 
	 @Override
	public Page<MenuItem> findSimilarItems(String regex, Pageable pageable) {
		
		return menuItemRepository.findByCategorysMatching(regex, pageable);
	}
}
