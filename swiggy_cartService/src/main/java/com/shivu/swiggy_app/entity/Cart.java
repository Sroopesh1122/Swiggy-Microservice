package com.shivu.swiggy_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cart")
@Data

public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer cartId;

	
	private Integer userId;

	private Integer menuItemId;

}
