package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest
{
  private String token;
  private String role;
}
