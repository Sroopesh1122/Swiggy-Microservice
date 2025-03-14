package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class PasswordResetRequest 
{
  private String token;
  private String role;
  private String newPassword;
}
