package com.shivu.swiggy_app.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shivu.swiggy_app.exception.PaymentException;

import lombok.Data;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Value("${RAZORPAY_API_KEY}")
	private String razorPayApiKey;

	@Value("${RAZORPAY_SECRET_KEY}")
	private String razorPaySecretKey;

	@PostMapping("/create/order")
	public ResponseEntity<?> createPayzorpayOrder(@RequestBody CreateOrderRequest request) {
		Map<String, String> response = new HashMap<>();

		try {
			RazorpayClient razorpayClient = new RazorpayClient(razorPayApiKey, razorPaySecretKey);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", (int) (request.getAmount() * 100)); // Amount in paise
			orderRequest.put("currency", "INR");
			Order order = razorpayClient.orders.create(orderRequest);
			response.put("status", "success");
			response.put("orderId", order.get("id").toString());

		} catch (RazorpayException e) {
			e.printStackTrace();
			throw new PaymentException("Payment failed");
		}

		return ResponseEntity.ok(response);

	}
	
	@PostMapping("/verify")
	public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerficationRequest request)
	{
		
		Map<String, String> response = new HashMap<>();
		
		JSONObject params = new JSONObject();
        params.put("razorpay_payment_id", request.getRazorpayPaymentId());
        params.put("razorpay_order_id", request.getRazorpayOrderId());
        params.put("razorpay_signature",request.getRazorpaySignature());

        // Verify the payment signature
        boolean isValid = false;
        try {
			isValid = Utils.verifyPaymentSignature(params, razorPaySecretKey);
		} catch (RazorpayException e) {
			e.printStackTrace();
			throw new PaymentException("Payment Failed");
		}
        
        if(isValid)
        {
          response.put("status", "success");
          response.put("message", "Payment Verified Successffully");
        }else {
        	response.put("status", "failed");
            response.put("message", "Payment Verified Failed");
        }
        return ResponseEntity.ok(response);
            
	}

}

@Data
class CreateOrderRequest {

	private Double amount;

}

@Data
class PaymentVerficationRequest{
  private String razorpayPaymentId;
  private String razorpayOrderId;
  private String razorpaySignature;

	
}
