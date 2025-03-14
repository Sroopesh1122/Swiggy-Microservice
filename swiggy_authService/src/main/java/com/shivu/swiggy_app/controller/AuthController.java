package com.shivu.swiggy_app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.EmailStore;
import com.shivu.swiggy_app.exception.AuthException;
import com.shivu.swiggy_app.feignClient.DeliveryPartnerServiceClient;
import com.shivu.swiggy_app.feignClient.EmailServiceClient;
import com.shivu.swiggy_app.feignClient.RestaurantServiceClient;
import com.shivu.swiggy_app.feignClient.UserServiceClient;
import com.shivu.swiggy_app.repo.EmailStoreRepository;
import com.shivu.swiggy_app.request.CreateDeliveryPartnerRequest;
import com.shivu.swiggy_app.request.DeliveryPartner;
import com.shivu.swiggy_app.request.DeliveryPartnerSigninRequest;
import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.OTPRequest;
import com.shivu.swiggy_app.request.OTPVerify;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.RestauarantSignIn;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.RestaurantSignUpRequest;
import com.shivu.swiggy_app.request.SendEmailRequest;
import com.shivu.swiggy_app.request.User;
import com.shivu.swiggy_app.request.UserSignInRequest;
import com.shivu.swiggy_app.request.UserSignUpRequest;
import com.shivu.swiggy_app.util.JwtUtil;
import com.shivu.swiggy_app.util.MessageReader;
import com.shivu.swiggy_app.util.RandomGenerator;

import feign.FeignException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserServiceClient userServiceClient;

	@Autowired
	private RestaurantServiceClient restaurantServiceClient;

	@Autowired
	private DeliveryPartnerServiceClient deliveryPartnerServiceClient;

	@Autowired
	private EmailServiceClient emailServiceClient;
	
	
	@Autowired
	private EmailStoreRepository emailStoreRepository;


	@Value("${FRONTEND_URL}")
	private String frontendUrl;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/user/signup")
	public ResponseEntity<?> userSignUpHandler(@Valid @RequestBody UserSignUpRequest request,
			BindingResult bindingResult) {

		// Checking for data
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid user Data");
		}
		User user = new User();
		user.setAddress(request.getAddress());
		user.setCreatedAt(LocalDateTime.now());
		user.setEmail(request.getEmail());
		user.setName(request.getName());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhoneNumber(request.getPhoneNumber());

		// save user
		User createdUser = null;

		try {
			createdUser = userServiceClient.createUser(user);
		} catch (FeignException e) {
			e.printStackTrace();
			throw new AuthException(MessageReader.getMessage(e));
		}
	
		Map<String, Object> claims = new HashMap<>();
		claims.put("email", createdUser.getEmail());
		claims.put("role", "customer");

		String generatedToken = jwtUtil.generateToken(createdUser.getEmail(), claims);

		Map<String, String> response = new HashMap<>();
		response.put("status", "success");
		response.put("token", generatedToken);
		return ResponseEntity.ok(response);

	}

	@PostMapping("/user/signin")
	public Map<String, String> userSignInHandler(@Valid @RequestBody UserSignInRequest request,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid Input");
		}
	
		User findUser = null;

		try {
			findUser = userServiceClient.getUserByEmail(request.getEmail());
		} catch (FeignException e) {
			System.out.println("shivu here lo");
			throw new AuthException(MessageReader.getMessage(e));
		}

		if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
			throw new AuthException("Incorrect password");
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "customer");
		claims.put("email", findUser.getEmail());

		Map<String, String> response = new HashMap<>();
		response.put("status", "success");
		response.put("token", jwtUtil.generateToken(findUser.getEmail(), claims));

		return response;

	}

	@PostMapping("/restaurant/singup")
	public Map<String, String> restaurantSignUp(@Valid @RequestBody RestaurantSignUpRequest request,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid Input data");
		}

		Restaurant createdRestauarnt=null;
		try {
			createdRestauarnt = restaurantServiceClient.createUser(request);
		} catch (FeignException e) {
			throw new AuthException(MessageReader.getMessage(e));
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "restaurant");
		claims.put("email", createdRestauarnt.getEmail());

		Map<String, String> response = new HashMap<>();
		response.put("success", "true");
		response.put("token", jwtUtil.generateToken(createdRestauarnt.getEmail(), claims));
		return response;

	}

	@PostMapping("/restaurant/singin")
	public Map<String, String> restaurantSignIn(@Valid @RequestBody RestauarantSignIn request,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid Data");
		}
		Restaurant findRestaurant = null;
		
		try {
			findRestaurant =  restaurantServiceClient.getUserByEmail(request.getEmail());
		} catch (FeignException e) {
			e.printStackTrace();
			throw new AuthException(MessageReader.getMessage(e));
		}
		
		if (!passwordEncoder.matches(request.getPassword(), findRestaurant.getPassword())) {
			throw new AuthException("Incorrect password");
		}
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "restaurant");
		claims.put("email", findRestaurant.getEmail());
		Map<String, String> response = new HashMap<>();
		response.put("success", "true");
		response.put("token", jwtUtil.generateToken(findRestaurant.getEmail(), claims));
		return response;
	}
	
	
	
	@PostMapping("/delivery/signup")
	public ResponseEntity<?> createDeliveryPartner(@Valid @RequestBody CreateDeliveryPartnerRequest request,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid input data");
		}
		
	    DeliveryPartner	createdDeliveryPartner=null;
	    
	    System.out.println(request);
	    
	    
	    try {
			createdDeliveryPartner = deliveryPartnerServiceClient.createDeliveryPartner(request);
		} catch (FeignException e) {
			e.printStackTrace();
			throw new AuthException(MessageReader.getMessage(e));
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "delivery");
		claims.put("email", createdDeliveryPartner.getEmail());

		Map<String, Object> response = new HashMap<>();

		response.put("status", "success");
		response.put("token", jwtUtil.generateToken(createdDeliveryPartner.getEmail(), claims));

		return ResponseEntity.ok(response);

	}
//
	@PostMapping("/delivery/signin")
	public ResponseEntity<?> deliveryPartnerSignIn(@RequestBody DeliveryPartnerSigninRequest request) {
		if (request.getEmail() == null || request.getPassword() == null) {
			throw new AuthException("Invalid input data");
		}

        DeliveryPartner findDeliveryPartner=null;
		
		try {
			findDeliveryPartner = deliveryPartnerServiceClient.getDeliveryPartnerByEmail(request.getEmail());
		} catch (FeignException e) {
			e.printStackTrace();
			throw new AuthException(MessageReader.getMessage(e));
		}

		if (!passwordEncoder.matches(request.getPassword(), findDeliveryPartner.getPassword())) {
			throw new AuthException("Incorrect Pasword");
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "delivery");
		claims.put("email", findDeliveryPartner.getEmail());
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("token", jwtUtil.generateToken(findDeliveryPartner.getEmail(), claims));
		response.put("userDetails", findDeliveryPartner);
		return ResponseEntity.ok(response);

	}

	
	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPasswordHanlder(@RequestBody ForgotPasswordRequest request) {
		String role = request.getRole();
		String email = request.getEmail();

		System.out.println("Here");
		 
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();

		forgotPasswordRequest.setEmail(email);
		forgotPasswordRequest.setRole(role);
		
		
		Map<String ,Object> response=null;
		
		
		try {
			
			if (role.equals("customer")) {
				  response=	userServiceClient.forgotPassword(forgotPasswordRequest);
			} 
			else if (role.equals("restaurant")) 
			{
			   response = restaurantServiceClient.forgotPassword(forgotPasswordRequest);
			} 
			else if (role.equals("delivery")) {
				response = deliveryPartnerServiceClient.forgotPassword(forgotPasswordRequest);
			}else {
				throw new AuthException("Invalid User");
			}
			
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new AuthException(MessageReader.getMessage(e));
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/resetpassword")
	public ResponseEntity<?> resetPasswordHanlder(@RequestBody PasswordResetRequest request) {

		String role = request.getRole();
		
		
		try {
			if (role.equals("customer")) {
				return ResponseEntity.ok(userServiceClient.resetPassword(request));

			} else if (role.equals("restaurant")) {
				return ResponseEntity.ok(restaurantServiceClient.resetPassword(request));
			} else if (role.equals("delivery")) {
				return ResponseEntity.ok(deliveryPartnerServiceClient.resetPassword(request));
			} else {
				throw new AuthException("Invalid User");
			}
		} catch (FeignException e) {
			throw new AuthException(MessageReader.getMessage(e));
		}	
	}

	@PostMapping("/otp")
	public Map<String, String> requestOTP(@Valid @RequestBody OTPRequest request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid Data");
		}
		
		if (request.getUserType().equalsIgnoreCase("customer")) {
			User user = null;

			try {
				user = userServiceClient.getUserByEmail(request.getEmail());
			} catch (Exception e) {
			}
			if (user != null) {
				throw new AuthException("Email Already Exists");
			}
		} else if (request.getUserType().equalsIgnoreCase("restaurant")) {
			Restaurant restaurant = null;
			
			try {
				restaurant = restaurantServiceClient.getUserByEmail(request.getEmail());
			} catch (Exception e) {
			}
			
			if(restaurant!=null)
			{
				throw new AuthException("Email Already exists");
			}
			
		} else if (request.getUserType().equalsIgnoreCase("delivery")) {
			DeliveryPartner deliveryPartner=null;
			
			try {
				deliveryPartner = deliveryPartnerServiceClient.getDeliveryPartnerByEmail(request.getEmail());
			} catch (Exception e) {
				
			}
			
			if(deliveryPartner!=null)
			{
				throw new AuthException("Email Already exists");
			}
		}
		String generatedOTP = RandomGenerator.generateNumberString(6);
		String subject = "Email Verification";
		String body = "Dear User,\n\n"
				+ "Thank you for signing up. Please use the OTP below to verify your email and complete the registration process.\n\n"
				+ "Your OTP: " + generatedOTP + "\n\n"
				+ "This OTP is valid for 10 minutes. Do not share it with anyone.\n\n"
				+ "If you did not request this, please ignore this email.\n\n" + "Best regards,\n" + "Team Swiggy";

		String htmlBody = "<html>"
				+ "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #fff;'>"
				+ "<div style='max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>"
				+ "<h2 style='color: #333; text-align: center;'>Email Verification</h2>"
				+ "<p style='font-size: 16px; color: #555;'>Dear User,</p>"
				+ "<p style='font-size: 16px; color: #555;'>Thank you for signing up. Please use the OTP below to verify your email and complete the registration process.</p>"
				+ "<div style='text-align: center; margin: 20px 0;'>"
				+ "<span style='font-size: 24px; font-weight: bold; color: #4CAF50; padding: 10px 20px; border-radius: 5px; background: #eaf7ea; display: inline-block;'>"
				+ generatedOTP + "</span>" + "</div>"
				+ "<p style='font-size: 16px; color: #555;'>This OTP is valid for <strong>10 minutes</strong>. Do not share it with anyone.</p>"
				+ "<p style='font-size: 16px; color: #555;'>If you did not request this, please ignore this email.</p>"
				+ "<br>" + "<p style='font-size: 16px; color: #555;'>Best regards,<br><strong>Team Swiggy</strong></p>"
				+ "</div>" + "</body>" + "</html>";

		SendEmailRequest sendEmailRequest = new SendEmailRequest();
		sendEmailRequest.setBody(htmlBody);
		sendEmailRequest.setEmail(request.getEmail());
		sendEmailRequest.setSubject(subject);

		emailServiceClient.sendEmail(sendEmailRequest);
		
		EmailStore existingEmailOtp = emailStoreRepository.findByEmail(request.getEmail());

		if (existingEmailOtp != null ) {
           
			existingEmailOtp.setOtp(generatedOTP);
			emailStoreRepository.save(existingEmailOtp);
			
		}else {
			EmailStore emailStore =  new EmailStore();
			emailStore.setAttempt(0);
			emailStore.setEmail(request.getEmail());
			emailStore.setOtp(generatedOTP);
			emailStore.setUserType(request.getUserType());
			
			emailStoreRepository.save(emailStore);
		}

		Map<String, String> apiResponse = new HashMap<>();
		apiResponse.put("success", "true");
		apiResponse.put("email", request.getEmail());
		return apiResponse;
	}

	@PostMapping("/otp/verify")
	public Map<String, String> verifyOTP(@Valid @RequestBody OTPVerify request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new AuthException("Invalid Data");
		}
		
		EmailStore existingEmailStore = emailStoreRepository.findByEmail(request.getEmail());
		
		if(existingEmailStore==null)
		{
			throw new AuthException("Invalid Email");
		}
		
		if(!existingEmailStore.getOtp().equals(request.getOtp()))
		{
			throw new AuthException("Invalid Otp");
		}
		
		emailStoreRepository.deleteById(existingEmailStore.getId());

		Map<String, String> apiResponse = new HashMap<>();
		apiResponse.put("success", "true");
		apiResponse.put("message", "OTP verified Successfully");
		return apiResponse;

	}

}
