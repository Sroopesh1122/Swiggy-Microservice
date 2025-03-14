package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MenuItem
{
	private Integer itemId;

	private String name;

	private String description;

	private Double price;

	private Integer available;

	private String category;

	private String img;

	private Double rating;
	
	private Integer discount;

	private Integer reviewsCount;

	private LocalDateTime createdAt;

	private Integer restaurantId;
}
