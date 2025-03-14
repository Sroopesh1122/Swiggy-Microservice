package com.shivu.swiggy_app.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.SEQUENCE)
	private int restaurantId;
	
	private String name;
	
	private String address;
	
	private String phoneNumber;
	
	private String password;
	
	private String email;
	
	private Double rating;
	
	private Integer reviewsCount;
	
	@CreatedDate
	private LocalDateTime createdAt;
	
    private String passwordResetToken;
	
	private LocalDateTime passwordExpiredBy;
	
//	@JsonIgnore //To ignore this field from being converting to json
//	@OneToMany(mappedBy = "restaurant",cascade = CascadeType.ALL ,fetch = FetchType.LAZY )
//	private List<MenuItem> menuItems;
	
}
