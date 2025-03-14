package com.shivu.swiggy_app.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;

public class MessageReader
{
   public static String getMessage(FeignException e)
   {
	   String message = "Something Went Wrong";
	   
	   try {
			String responseBody = e.contentUTF8(); // Convert response to String
			ObjectMapper objectMapper = new ObjectMapper();

			// Convert JSON to Map (assuming response is a JSON object)
			Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);

			String errorMessage = (String) errorResponse.get("message");
			String errorType = (String) errorResponse.get("type");

			System.out.println("Error Type: " + errorType);
			System.out.println("Error Message: " + errorMessage);
			message = errorMessage;

		} catch (Exception ex) {
			System.err.println("Error parsing Feign error response: " + ex.getMessage());
		}
	   
	   return message;
   }
}
