package com.incture.ecommerceBackend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.ecommerceBackend.Entity.Order;
import com.incture.ecommerceBackend.Entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	// Finds all orders placed by a specific customer
	List<Order> findByUser(User user);
}