package com.shivu.swiggy_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler 
{
   @ExceptionHandler(PaymentException.class)
   public ResponseEntity<?> handleUserException(PaymentException userException)
   {
	   Map<String, Object> response = new HashMap<>();
	   response.put("type","User Exception");
	   response.put("message",userException.getMessage());
	   return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
   }
}
