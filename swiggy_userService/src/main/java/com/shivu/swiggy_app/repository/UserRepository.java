package com.shivu.swiggy_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shivu.swiggy_app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> 
{
   public Optional<User> findByEmail(String email);
   
   public Optional<User> findByPasswordResetToken(String token);
}