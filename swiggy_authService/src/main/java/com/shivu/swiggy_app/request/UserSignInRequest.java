package com.shivu.swiggy_app.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignInRequest 
{
  @NotBlank(message = "Required")	
  private String email;
  
  @NotBlank(message = "Required")
  private String password;
}
