package com.shivu.swiggy_emailService.controller;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_emailService.EmailUtils;
import com.shivu.swiggy_emailService.exception.EmailException;
import com.shivu.swiggy_emailService.request.SendEmailRequest;

@RestController
@RequestMapping("/api/email")
public class EmailController {

	@Autowired
	private EmailUtils emailUtils;

	@PostMapping("/send")
	public ResponseEntity<?> sendEmailNotification(@RequestBody SendEmailRequest request) {
		Map<String, Object> response = new HashMap<>();

		try {
//			emailUtils.sendEmail(request.getEmail(), request.getSubject(), request.getBody());
			emailUtils.sendHtmlEmail(request.getEmail(), request.getSubject(), request.getBody());

		} catch (MessagingException e) {
			throw new EmailException("Something Went Wrong", HttpStatus.BAD_REQUEST);
		}
		response.put("status", "success");
		response.put("message", "Email Send Successfully");
		return ResponseEntity.ok(response);
	}

}
