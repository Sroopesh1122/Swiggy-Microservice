package com.shivu.swiggy_app.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shivu.swiggy_app.entity.Restaurant;
import com.shivu.swiggy_app.exception.RestaurantException;
import com.shivu.swiggy_app.feign.EmailServiceClient;
import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.RestaurantSignUpRequest;
import com.shivu.swiggy_app.request.RestaurantUpdateRequest;
import com.shivu.swiggy_app.request.SendEmailRequest;
import com.shivu.swiggy_app.service.IRestaurantService;
import com.shivu.swiggy_app.service.RestaurantDetails;
import com.shivu.swiggy_app.util.RandomGenerator;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
	
	@Autowired
	private  IRestaurantService restaurantService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	
	@Autowired
	private EmailServiceClient emailServiceClient;
	
	

	
	@GetMapping("/top-rated")
	public List<Restaurant> topFiveRestaurant(){
		return restaurantService.getTopFiveRestaurants();
	}
	
	
	
	@GetMapping("/profile")
	@PreAuthorize("hasRole('RESTAURANT')")
	public Restaurant getRestaurantById(@AuthenticationPrincipal RestaurantDetails restaurantDetails)
	{
	   if(restaurantDetails ==null)	
	   {
		   throw new RestaurantException("Unauthenticated" );
	   }
	   
	   Restaurant restaurant = restaurantDetails.getUser();
	   return restaurant;
	}
	
	
	@GetMapping("/email")
	public Restaurant getUserByEmail(@RequestParam String email)
	{
	
		System.out.println("Called email method");
		
		Restaurant findRestaurant = restaurantService.findByEmail(email);
		
		if(findRestaurant == null)
		{
			throw new RestaurantException("User Not Found");
		}
		
		return findRestaurant;
		
	}
	
	
	@GetMapping("/restaurantId/{id}")
	public Restaurant getRestaurantById(@PathVariable("id") Integer rId)
	{
		
		Restaurant restaurant = restaurantService.findById(rId);
		if(restaurant == null)
		{
			throw new RestaurantException("Restaurant not found");
		}
		restaurant.setPassword(null);
		return restaurant;
	}
	
	@GetMapping("/{id}")
	public Restaurant getRestaurant(@PathVariable("id") Integer rId)
	{
		
		Restaurant restaurant = restaurantService.findById(rId);
		if(restaurant == null)
		{
			throw new RestaurantException("Restaurant not found");
		}
		restaurant.setPassword(null);
		return restaurant;
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody RestaurantSignUpRequest request)
	{
		
		if(restaurantService.findByEmail(request.getEmail()) !=null)
		{
			throw new RestaurantException("Email Already Exixts");
		}
		
		Restaurant restaurant =  new Restaurant();
		restaurant.setAddress(request.getAddress());
		restaurant.setCreatedAt(LocalDateTime.now());
		restaurant.setEmail(request.getEmail());
		restaurant.setName(request.getName());
		restaurant.setPassword(passwordEncoder.encode(request.getPassword()));
		restaurant.setPasswordResetToken(null);
		restaurant.setPasswordResetToken(null);
		restaurant.setPhoneNumber(request.getPhoneNumber());
		restaurant.setRating(5.0);
		restaurant.setReviewsCount(1);
		
		Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);
		createdRestaurant.setPassword(null);
		
		return ResponseEntity.ok(createdRestaurant);
	}
    
	
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody RestaurantUpdateRequest request)
	{
	    Restaurant findRestaurant = restaurantService.findById(request.getRestaurantId());
	    
	    if(findRestaurant ==null)
	    {
	    	throw new RestaurantException("User not found");
	    }
		findRestaurant.setAddress(request.getAddress());
		findRestaurant.setName(request.getName());
		findRestaurant.setPassword(request.getPassword());
		findRestaurant.setPasswordResetToken(null);
		findRestaurant.setPhoneNumber(request.getPhoneNumber());
		restaurantService.upadetRestaurant(findRestaurant);
		return ResponseEntity.ok(findRestaurant);
	}
	
	@PutMapping("/rating/update")
	public ResponseEntity<?> updateUserRating(@RequestBody Restaurant request)
	{
	    Restaurant findRestaurant = restaurantService.findById(request.getRestaurantId());
	    
	    if(findRestaurant ==null)
	    {
	    	throw new RestaurantException("User not found");
	    }
		findRestaurant.setRating(request.getRating());
		findRestaurant.setReviewsCount(request.getReviewsCount());
	    findRestaurant=	restaurantService.upadetRestaurant(findRestaurant);
		return ResponseEntity.ok(findRestaurant);
	}
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request)
	{
		
		Restaurant findRestaurant = restaurantService.findByPasswordResetToken(request.getToken());
		
		if(findRestaurant == null)
		{
			throw new RestaurantException("Invalid Token");
		}
		
		String resetToken = RandomGenerator.generateSecureToken();
		
		String subject = "Password Reset Request";
		String resetLink = frontendUrl+"auth/reset-password?token=" + resetToken + "&role="+request.getRole();

		String body = "Dear "+findRestaurant.getName()+",\n\n"
				+ "We received a request to reset your password. Please click the link below to set a new password:\n\n"
				+ resetLink + "\n\n"
				+ "This link is valid for 10 minutes. If you did not request this, please ignore this email.\n\n"
				+ "Best regards,\n" + "Team Swiggy";
		
		SendEmailRequest sendEmailRequest = new SendEmailRequest();
		sendEmailRequest.setBody(body);
		sendEmailRequest.setEmail(findRestaurant.getEmail());
		sendEmailRequest.setSubject(subject);
		
		emailServiceClient.sendEmail(sendEmailRequest);
		
		findRestaurant.setPasswordResetToken(resetToken);
		findRestaurant.setPasswordExpiredBy(LocalDateTime.now());
         
		restaurantService.upadetRestaurant(findRestaurant);
		
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset linksent successfully to " + findRestaurant.getEmail());
		return ResponseEntity.ok(response);
		
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
		
		Restaurant findRestaurant = restaurantService.findByPasswordResetToken(request.getToken());
		
		if(findRestaurant ==null)
		{
			throw new RestaurantException("Invalid Token");
		}
		
		
		if(LocalDateTime.now().isAfter(findRestaurant.getPasswordExpiredBy()))
		{
			throw new RestaurantException("Token Expired");
		}
		
		findRestaurant.setPassword(passwordEncoder.encode(request.getNewPassword()));
		findRestaurant.setPasswordExpiredBy(null);
		findRestaurant.setPasswordResetToken(null);
		findRestaurant = restaurantService.upadetRestaurant(findRestaurant);
		
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset successfully");
		return ResponseEntity.ok(response);
	}
	

}
