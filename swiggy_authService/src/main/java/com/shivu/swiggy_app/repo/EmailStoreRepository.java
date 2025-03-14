package com.shivu.swiggy_app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.EmailStore;

@Repository
public interface EmailStoreRepository extends JpaRepository<EmailStore, Integer> {
	
	public EmailStore findByEmail(String email);

}
