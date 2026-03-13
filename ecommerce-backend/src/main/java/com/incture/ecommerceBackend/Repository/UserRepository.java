package com.incture.ecommerceBackend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.ecommerceBackend.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	// finds a user by their email during login
	Optional<User> findByEmail(String email);
}