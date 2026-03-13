package com.incture.ecommerceBackend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	// Finds the shopping cart that belongs to a specific user
	Optional<Cart> findByUser(User user);
}