package com.shivu.swiggy_app.request;


import lombok.Data;

@Data
public class RestaurantSignUpRequest 
{
  private String name;
  
  private String email;
  
  private String password;
  
  private String address;
  
  private String phoneNumber;
  
}
