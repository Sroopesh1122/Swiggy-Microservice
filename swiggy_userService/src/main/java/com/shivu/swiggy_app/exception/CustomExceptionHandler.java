package com.shivu.swiggy_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler 
{
   @ExceptionHandler(UserException.class)
   public ResponseEntity<?> handleUserException(UserException userException)
   {
	   Map<String, Object> response = new HashMap<>();
	   response.put("type","User Exception");
	   response.put("message",userException.getMessage());
	   return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
   }
   
   
   @ExceptionHandler(UsernameNotFoundException.class)
   public ResponseEntity<?> handleUserNotFOundException(UsernameNotFoundException userException)
   {
	   Map<String, Object> response = new HashMap<>();
	   response.put("type","User Exception");
	   response.put("message",userException.getMessage());
	   return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
   }
}
