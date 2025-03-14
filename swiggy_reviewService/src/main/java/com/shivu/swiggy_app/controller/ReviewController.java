package com.shivu.swiggy_app.controller;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.Review;
import com.shivu.swiggy_app.exception.ReviewException;
import com.shivu.swiggy_app.feignClient.MenuItemClient;
import com.shivu.swiggy_app.feignClient.OrderServiceClient;
import com.shivu.swiggy_app.feignClient.RestaurantServiceClient;
import com.shivu.swiggy_app.feignClient.UserServiceClient;
import com.shivu.swiggy_app.request.AddReviewRequest;
import com.shivu.swiggy_app.request.MenuItem;
import com.shivu.swiggy_app.request.Order;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.User;
import com.shivu.swiggy_app.service.IReviewService;
import com.shivu.swiggy_app.service.PaginationService;
import com.shivu.swiggy_app.util.MessageReader;

import feign.FeignException;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

	@Autowired
	private IReviewService reviewService;

	@Autowired
	private MenuItemClient menuItemClient;

	@Autowired
	private OrderServiceClient orderServiceClient;

	@Autowired
	private RestaurantServiceClient restaurantServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;

	@PostMapping("/")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> addReview(
			@RequestBody AddReviewRequest request) {
		
		User user = null;
		
		try {
			user = userServiceClient.getById(request.getUserId());
		} catch (FeignException e) {
			e.printStackTrace();
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		
		System.out.println("customer fetched");
	

		Order order = null;
		
		try {
			order = orderServiceClient.getById(request.getOrderId());
		} catch (FeignException e) {
			e.printStackTrace();
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		System.out.println("order fetched");

		
		if (order.getReviewed() == 1) {
			throw new ReviewException("Order Already reviewed");
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		Map<String, String> response = new HashMap<>();

		Review review = new Review();
		review.setComment(request.getComment());
		review.setCreatedAt(LocalDateTime.now());
		review.setRating(request.getRating());

		MenuItem menuItem = null;
		
		try {
			menuItem = menuItemClient.getMenuItemById(request.getItemId());
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new ReviewException(MessageReader.getMessage(e));
			
		}
		
		System.out.println("menu fetched "+menuItem);
		
		Integer prevReviewCount = menuItem.getReviewsCount();
		Double prevRating = menuItem.getRating();
		Double newRating = ((prevRating * prevReviewCount) + request.getRating()) / (prevReviewCount + 1);
		menuItem.setRating(Double.valueOf(decimalFormat.format(newRating)));
		menuItem.setReviewsCount(prevReviewCount + 1);
		try {
			 menuItemClient.updateMenuItemRating(menuItem);
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println("menuUpdated"+menuItem);
		
		review.setMenuItemId(menuItem.getItemId());

		Restaurant restaurant = null;
		
		try {
			restaurant = restaurantServiceClient.getRestaurantById(menuItem.getRestaurantId());
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		System.out.println("restaurabt fetched");
		prevReviewCount = restaurant.getReviewsCount();
		prevRating = restaurant.getRating();
		newRating = ((prevRating * prevReviewCount) + request.getRating()) / (prevReviewCount + 1);

		restaurant.setRating(Double.valueOf(decimalFormat.format(newRating)));
		restaurant.setReviewsCount(prevReviewCount + 1);

		try {
			restaurant = restaurantServiceClient.updateResturant(restaurant);
		} catch (FeignException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		System.out.println("rest updated");
		review.setRestaurantId(restaurant.getRestaurantId());
		review.setUserId(request.getUserId());

		order.setReviewed(1);
		try {
			orderServiceClient.updateOrder(order);
		} catch (FeignException e) {
			throw new ReviewException(MessageReader.getMessage(e));
		}

		System.out.println("order updated");
		reviewService.add(review);

		response.put("status", "success");
		response.put("message", "Order Reviewed Successfully");

		return ResponseEntity.ok(response);

	}

	@GetMapping("/menuItem")
	public ResponseEntity<?> getAllReviewsByItemId(
			@RequestParam Integer id , 
			@RequestParam(required = false ,defaultValue = "1") Integer page , 
			@RequestParam(required = false ,defaultValue = "10") Integer limit)
	{
		
		page = page - 1;
		Pageable pageable = PageRequest.of(page, limit,Sort.by("reviewId").descending());
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("data", reviewService.getAllReviewsByItemId(id, pageable));
		return ResponseEntity.ok(response);		
	}
	

	@GetMapping("/restaurant")
	@PreAuthorize("hasRole('RESTAURANT')")
	public ResponseEntity<?> getAllReviewsOfRestaurant(
			@RequestParam Integer restaurantId,
			@RequestParam(required = false , defaultValue="1" )Integer page,
			@RequestParam(required = false , defaultValue="10") Integer limit)
	{
		
		
		Restaurant restaurant = null;
		
		try {
			restaurant = restaurantServiceClient.getRestaurantById(restaurantId);
		} catch (FeignException e) {
			// TODO: handle exception
			throw new ReviewException(MessageReader.getMessage(e));
		}
		
		
		
		page = page -1;
		
		Pageable pageable = PageRequest.of(page, limit, Sort.by("reviewId").descending());
		
		Page<Review> reviews = reviewService.getAllReviewsByRestaurantId(restaurantId, pageable);
		
		
		Map<String, Object> response = PaginationService.getPageData(reviews);
		
	   List<Map<String,Object>> reviewList =	reviews.stream().map((review)->{
			Map<String, Object> reviewMap =  new HashMap<>();
			reviewMap.put("review",review);
			reviewMap.put("reviewedBy", userServiceClient.getById(review.getUserId()));
			reviewMap.put("restaurant",restaurantServiceClient.getRestaurantById(review.getRestaurantId()));
			reviewMap.put("menuItem",menuItemClient.getMenuItemById(review.getMenuItemId()));
			return reviewMap;
		}).collect(Collectors.toList());
	   
	   response.put("reviews", reviewList);
	   
	   return ResponseEntity.ok(response);
	
		
	}

}
