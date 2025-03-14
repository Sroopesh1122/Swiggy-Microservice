package com.shivu.swiggy_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shivu.swiggy_app.util.MessageReader;

import feign.FeignException.FeignClientException;

@RestControllerAdvice
public class CustomExceptionHandler 
{
   @ExceptionHandler(CartException.class)
   public ResponseEntity<?> handleUserException(CartException userException)
   {
	   Map<String, Object> response = new HashMap<>();
	   response.put("type","Cart Exception");
	   response.put("message",userException.getMessage());
	   return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
   }
   
   
   @ExceptionHandler(FeignClientException.class)
   public ResponseEntity<?> handleServicesException(FeignClientException feignException)
   {
	   Map<String, Object> response = new HashMap<>();
	   response.put("type","Cart Exception");
	   response.put("message",MessageReader.getMessage(feignException));
	   return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
   }
}
