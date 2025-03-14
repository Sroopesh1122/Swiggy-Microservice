package com.shivu.swiggy_app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.User;
import com.shivu.swiggy_app.exception.UserException;
import com.shivu.swiggy_app.feign.EmailServiceClient;
import com.shivu.swiggy_app.request.ForgotPasswordRequest;
import com.shivu.swiggy_app.request.PasswordResetRequest;
import com.shivu.swiggy_app.request.SendEmailRequest;
import com.shivu.swiggy_app.request.UserCreateRequest;
import com.shivu.swiggy_app.request.UserUpdateRequest;
import com.shivu.swiggy_app.service.CustomerDetails;
import com.shivu.swiggy_app.service.IUserService;
import com.shivu.swiggy_app.util.MessageReader;
import com.shivu.swiggy_app.util.RandomGenerator;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController
{
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailServiceClient emailServiceClient;
	
	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	
	@GetMapping("/profile")
    public ResponseEntity<?> getProfileByToken(HttpServletRequest request)
    {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if(authentication==null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser"))
    	{
    		throw new UsernameNotFoundException("Unauthenticated");
    	}
    	
    	CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
		User user = customerDetails.getUser();
		User findUser = userService.findById(user.getUserId());	 
		findUser.setPassword(null); 
    	return ResponseEntity.ok(findUser);	
    }
	
	
	
	//Api gateway uses this controller to get User by email
	@GetMapping("/email")
	public ResponseEntity<?> getUserByEmail(@RequestParam String email)
	{
		User user = userService.findByEmail(email);
		if(user ==null)
		{
			throw new UserException("User Not Found");
		}
		
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/userId/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id)
	{
		User user = userService.findById(id);
		if(user ==null)
		{
			throw new UserException("User Not Found");
		}
		
		return ResponseEntity.ok(user);
	}

	
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request)
	{
		
		System.out.println("Inside user creation");
		
		if(userService.findByEmail(request.getEmail()) !=null)
		{
			throw new UserException("Email Already Exists");
		}
			
		User user = new User();
		user.setAddress(request.getAddress());
		user.setCreatedAt(request.getCreatedAt());
		user.setEmail(request.getEmail());
		user.setName(request.getName());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPasswordResetToken(null);
		user.setPasswordExpiredBy(null);
		user.setPhoneNumber(request.getPhoneNumber());
		user = userService.createUser(user);
		return ResponseEntity.ok(user);
	}
    
	
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request)
	{
	    User findUser = userService.findById(request.getUserId());
	    
	    if(findUser ==null)
	    {
	    	throw new UserException("User not found");
	    }
		findUser.setAddress(request.getAddress());
		findUser.setName(request.getName());
		findUser.setPassword(request.getPassword());
		findUser.setPasswordResetToken(null);
		findUser.setPhoneNumber(request.getPhoneNumber());
		return ResponseEntity.ok(findUser);
	}
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request)
	{
		
		User findUser = userService.findByEmail(request.getEmail());
	
		if(findUser == null)
		{
			throw new UserException("Email not exists");
		}
		
		String resetToken = RandomGenerator.generateSecureToken();
		
		String subject = "Password Reset Request";
		String resetLink = frontendUrl+"auth/reset-password?token=" + resetToken + "&role="+request.getRole();

		String body = "Dear "+findUser.getName()+",\n\n"
				+ "We received a request to reset your password. Please click the link below to set a new password:\n\n"
				+ resetLink + "\n\n"
				+ "This link is valid for 10 minutes. If you did not request this, please ignore this email.\n\n"
				+ "Best regards,\n" + "Team Swiggy";
		
		SendEmailRequest sendEmailRequest = new SendEmailRequest();
		sendEmailRequest.setBody(body);
		sendEmailRequest.setEmail(findUser.getEmail());
		sendEmailRequest.setSubject(subject);
		
		try {
			emailServiceClient.sendEmail(sendEmailRequest);
		} catch (FeignException e) {
			throw new UserException(MessageReader.getMessage(e));
		}
		
		findUser.setPasswordResetToken(resetToken);
		findUser.setPasswordExpiredBy(LocalDateTime.now().plusMinutes(10));
         
		userService.updateUser(findUser);
		
		Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset linksent successfully to " + findUser.getEmail());
		return ResponseEntity.ok(response);
		
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
		
		User findUser = userService.findByPasswordRestToken(request.getToken());
		
		if(findUser ==null)
		{
			throw new UserException("Invalid Token");
		}
		
		if(LocalDateTime.now().isAfter(findUser.getPasswordExpiredBy()))
		{
			findUser.setPasswordExpiredBy(null);
			findUser.setPasswordResetToken(null);
		    findUser = userService.updateUser(findUser);	
			throw new UserException("Token Expired");
		}
		
		findUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
		findUser.setPasswordExpiredBy(null);
		findUser.setPasswordResetToken(null);
	    findUser = userService.updateUser(findUser); 
	    Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("message", "Password reset successfully");
		return ResponseEntity.ok(response);
	}
	
}
