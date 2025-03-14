package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
public class MenuItem {

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

	@CreatedDate
	private LocalDateTime createdAt;

	private Integer restaurantId;

}
