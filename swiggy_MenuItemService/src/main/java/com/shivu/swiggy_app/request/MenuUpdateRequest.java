package com.shivu.swiggy_app.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MenuUpdateRequest 
{
	  private Integer itemId;
	  private String name;
	  private String description;
	  private String category;
	  private Double price;
	  private Integer discount;
	  private String image;
	  private Integer available;
}
