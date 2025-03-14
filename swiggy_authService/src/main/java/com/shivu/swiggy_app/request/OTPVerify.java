package com.shivu.swiggy_app.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OTPVerify 
{
  @NotBlank
  @Email(message = "Invalid Email")
  private String email;
  
  @NotBlank
  private String otp;
}
