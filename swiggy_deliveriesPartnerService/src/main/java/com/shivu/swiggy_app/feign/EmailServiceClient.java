package com.shivu.swiggy_app.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shivu.swiggy_app.request.SendEmailRequest;

@FeignClient("swiggy-emailService")
public interface EmailServiceClient 
{
  @PostMapping("/api/email/send")	
  public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody SendEmailRequest request);
}
