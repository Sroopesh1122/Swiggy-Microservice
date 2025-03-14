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
@Table(name = "delivery_partners")
@Data
public class DeliveryPartner 
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer partnerId;
  
  private String name;
  
  private String phoneNumber;
  
  private String vehicleDetails;
  
  @CreatedDate
  private LocalDateTime createdAt;
  
  private String email;
  
  private String password;
  
  private String passwordResetToken;
	
  private LocalDateTime passwordExpiredBy;
  
  
}
