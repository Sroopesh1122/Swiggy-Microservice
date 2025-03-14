package com.shivu.swiggy_app.response;

import java.time.LocalDateTime;

import com.shivu.swiggy_app.request.User;

import lombok.Data;

@Data
public class ReviewAndUser {

	private Integer reviewId;

	private Integer rating;

	private String comment;

	private LocalDateTime createdAt;

	private Integer menuItemId;

	private User user;

	private Integer restaurantId;
}
