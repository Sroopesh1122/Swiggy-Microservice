package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class MenuItemRequest
{
  private String name;
  private String description;
  private String category;
  private Double price;
  private Integer discount;
  private String image;
  private Integer restaurantId;
}
