package com.shivu.swiggy_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shivu.swiggy_app.entity.Review;

public interface IReviewService
{
  public Review add(Review review);
  public Review update(Review review);
  public boolean delete(Integer reviewId);
  public Page<Review> getAllReviewsByItemId (Integer itemId ,Pageable pageable);
  public Page<Review> getAllReviewsByRestaurantId(Integer rId ,Pageable pageable );
  
}
