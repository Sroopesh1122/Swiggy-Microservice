package com.shivu.swiggy_emailService.request;

import lombok.Data;

@Data
public class SendEmailRequest
{
  private String email;
  private String body;
  private String subject;
  
}
