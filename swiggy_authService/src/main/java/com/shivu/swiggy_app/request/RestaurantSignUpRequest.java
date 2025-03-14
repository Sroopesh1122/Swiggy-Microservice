package com.shivu.swiggy_app.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantSignUpRequest 
{
  @NotBlank(message = "Required")	
  private String name;
  
  @Email(message = "Invalid Email")
  @NotBlank(message = "Required")
  private String email;
  
  @NotBlank(message = "Required")
  private String password;
  
  @NotBlank(message = "Required")
  private String address;
  
  @NotBlank(message = "Required")
  private String phoneNumber;
  
}
