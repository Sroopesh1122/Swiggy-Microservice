package com.shivu.swiggy_app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.entity.Review;
import com.shivu.swiggy_app.repository.ReviewRepository;

@Service
public class ReviewServiceImpl implements IReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	
	@Override
	public Review add(Review review) {
		return reviewRepository.save(review);
	}

	@Override
	public Review update(Review review) {
		
		return reviewRepository.save(review);
	}

	@Override
	public boolean delete(Integer reviewId) {
		reviewRepository.deleteById(reviewId);
		return true;
	}

	@Override
	public Page<Review> getAllReviewsByItemId(Integer itemId ,Pageable pageable) {
		Page<Review> reviews = reviewRepository.getAllReviewsByItemId(itemId , pageable);
		return reviews;
	}
	
	@Override
	public Page<Review> getAllReviewsByRestaurantId(Integer rId, Pageable pageable) {
		Page<Review> reviews = reviewRepository.getAllReviewsByRestaurantId(rId , pageable);
		return reviews;
	}

}
