package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class SendEmailRequest
{
  private String email;
  private String body;
  private String subject;
  
}
