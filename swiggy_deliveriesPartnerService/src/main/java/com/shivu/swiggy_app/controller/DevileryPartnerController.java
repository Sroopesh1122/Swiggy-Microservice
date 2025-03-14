package com.shivu.swiggy_app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.DeliveryPartner;
import com.shivu.swiggy_app.exception.DeliveryException;
import com.shivu.swiggy_app.feign.EmailServiceClient;
import com.shivu.swiggy_app.request.CreateDeliveryPartnerRequest;
import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.SendEmailRequest;
import com.shivu.swiggy_app.service.DeliveryDetails;
import com.shivu.swiggy_app.service.IDeliveryPartnerService;
import com.shivu.swiggy_app.util.MessageReader;
import com.shivu.swiggy_app.util.RandomGenerator;

import feign.FeignException;

import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/delivery")
public class DevileryPartnerController
{
	
	@Autowired
	private IDeliveryPartnerService deliveryPartnerService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	private EmailServiceClient emailServiceClient;
	
	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	

   @GetMapping("/profile")
   @PreAuthorize("hasRole('DELIVERY')")
   public ResponseEntity<?> getProfile(
		   @AuthenticationPrincipal DeliveryDetails deliveryDetails)
   { 
	    if(deliveryDetails ==null)
	    {
	    	throw new DeliveryException("Unauthenticated");
	    }
		DeliveryPartner deliveryPartner = deliveryDetails.getUser();
		deliveryPartner.setPassword(null);
		return ResponseEntity.ok(deliveryPartner);
		
   }
   
   
   @PostMapping("/create")
   public ResponseEntity<?> createDeliveryPartner(@RequestBody CreateDeliveryPartnerRequest request)
   {
	   DeliveryPartner findUser = deliveryPartnerService.getByEmail(request.getEmail());
	   
	   if(findUser!=null)
	   {
		   throw new DeliveryException("Email Already exists");
	   }
	   
	   System.out.println(request);
	   
	   DeliveryPartner createUser = new DeliveryPartner();
	   createUser.setCreatedAt(LocalDateTime.now());
	   createUser.setEmail(request.getEmail());
	   createUser.setName(request.getName());
	   createUser.setPassword(passwordEncoder.encode(request.getPassword()));
	   createUser.setPasswordExpiredBy(null);
	   createUser.setPasswordResetToken(null);
	   createUser.setPhoneNumber(request.getPhoneNumber());
	   createUser.setVehicleDetails(request.getVehicleNumber());
	   createUser = deliveryPartnerService.create(createUser);
	   return ResponseEntity.ok(createUser);
	   
   }
   
   
   @PostMapping("/forgotpassword")
   public ResponseEntity<?> updateDeliveryPartner(@RequestBody ForgotPasswordRequest request)
   {
	   
	   DeliveryPartner deliveryPartner = deliveryPartnerService.findByPasswordResetToken(request.getToken());
	   
	   if(deliveryPartner ==null)
	   {
		   throw new DeliveryException("Invalid Token");
	   }
	   
	   String resetToken = RandomGenerator.generateSecureToken();
		
		String subject = "Password Reset Request";
		String resetLink = frontendUrl+"auth/reset-password?token=" + resetToken + "&role="+request.getRole();

		String body = "Dear "+deliveryPartner.getName()+",\n\n"
				+ "We received a request to reset your password. Please click the link below to set a new password:\n\n"
				+ resetLink + "\n\n"
				+ "This link is valid for 10 minutes. If you did not request this, please ignore this email.\n\n"
				+ "Best regards,\n" + "Team Swiggy";
		
		SendEmailRequest sendEmailRequest = new SendEmailRequest();
		sendEmailRequest.setBody(body);
		sendEmailRequest.setEmail(deliveryPartner.getEmail());
		sendEmailRequest.setSubject(subject);
		
		try {
			emailServiceClient.sendEmail(sendEmailRequest);
		} catch (FeignException e) {
			// TODO: handle exception
			throw new DeliveryException(MessageReader.getMessage(e));
		}
	   
		deliveryPartner.setPasswordResetToken(resetToken);
		deliveryPartner.setPasswordExpiredBy(LocalDateTime.now().plusMinutes(10));
		
		deliveryPartnerService.update(deliveryPartner);
		
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset linksent successfully to " + deliveryPartner.getEmail());
		return ResponseEntity.ok(response);
   }
   
   
   @PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
		
		DeliveryPartner deliveryPartner = deliveryPartnerService.findByPasswordResetToken(request.getToken());
		
		if(deliveryPartner ==null)
		{
			throw new DeliveryException("Invalid Token");
		}
		
		deliveryPartner.setPassword(passwordEncoder.encode(request.getNewPassword()));
		deliveryPartner.setPasswordExpiredBy(null);
		deliveryPartner.setPasswordResetToken(null);
		deliveryPartner = deliveryPartnerService.update(deliveryPartner);
	    
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset successfully");
		return ResponseEntity.ok(response);
		
	}
	
   
   
   @GetMapping("/partnerId/{id}")
   public ResponseEntity<?> getDeliveryPartnerById(@PathVariable Integer id) {
      
	   DeliveryPartner deliveryPartner = deliveryPartnerService.getByd(id);
	   
	   if(deliveryPartner == null)
	   {
		   throw new DeliveryException("User Not Found");
	   }
	   return ResponseEntity.ok(deliveryPartner);  
   }
   
   
   @GetMapping("/id/{id}")
   public ResponseEntity<?> getDeliveryPartner(@PathVariable Integer id) {
      
	   DeliveryPartner deliveryPartner = deliveryPartnerService.getByd(id);
	   
	   if(deliveryPartner == null)
	   {
		   throw new DeliveryException("User Not Found");
	   }
	   return ResponseEntity.ok(deliveryPartner);  
   }
   
   
   @GetMapping("/email")
   public ResponseEntity<?> getDeliveryPartnerByEmail(@RequestParam String email) {
      
	   DeliveryPartner deliveryPartner = deliveryPartnerService.getByEmail(email);
	   
	   if(deliveryPartner == null)
	   {
		   throw new DeliveryException("User Not Found");
	   }  
	   return ResponseEntity.ok(deliveryPartner);  
   }
   
   
   
}
