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
@Table(name = "deliveries")
@Data
public class Deliveries
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer deliveryId;
  
  private String deliveryStatus;
  
  @CreatedDate
  private LocalDateTime assignedAt;
  
  private LocalDateTime deliveredAt;
  
  private String deliver_code;
  
  private Integer orderId;
  
  private Integer deliveryPartnerId ;
  
}
