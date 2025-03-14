package com.shivu.swiggy_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.shivu.swiggy_app.entity.User;
import com.shivu.swiggy_app.exception.UserException;
import com.shivu.swiggy_app.repository.UserRepository;

@Component
public class UserServiceImpl implements IUserService {
	
	
	private UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	

	@Override
	public User createUser(User user) {
		
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user) {
		
		return userRepository.save(user);
	}

	@Override
	public User findById(Integer userId) {
		
		return userRepository.findById(userId).orElse(null);
	}

	@Override
	public List<User> findAll() {
		
		return userRepository.findAll();
	}

	@Override
	public User findByEmail(String email) {		
		return userRepository.findByEmail(email).orElse(null);
	}
	
	@Override
	public User findByPasswordRestToken(String token) {
		return userRepository.findByPasswordResetToken(token).orElse(null);
	}
	

}
