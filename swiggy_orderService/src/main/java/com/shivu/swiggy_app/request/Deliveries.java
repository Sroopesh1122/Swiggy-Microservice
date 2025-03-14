package com.shivu.swiggy_app.request;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class Deliveries {

	  private Integer deliveryId;
	  
	  private String deliveryStatus;
	  
	  private LocalDateTime assignedAt;
	  
	  private LocalDateTime deliveredAt;
	  
	  private String deliver_code;
	  
	  private Integer orderId;
	  
	  private Integer deliveryPartnerId ;
	
	
}
