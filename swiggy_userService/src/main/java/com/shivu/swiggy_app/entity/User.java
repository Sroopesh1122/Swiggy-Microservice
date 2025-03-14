package com.shivu.swiggy_app.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int userId;

	private String name;

	@Column(unique = true)
	private String email;

	private String phoneNumber;

	private String address;

	private String password;

	@CreatedDate
	private LocalDateTime createdAt;
	
	private String passwordResetToken;
	
	private LocalDateTime passwordExpiredBy;
	
}
