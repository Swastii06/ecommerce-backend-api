package com.incture.ecommerceBackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public User save(User user) {
		return userRepository.save(user);
	}

	// Required by Spring Security to find the user during login
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
	}

	// --- NEW METHODS FOR FINAL SUBMISSION ---

	// Helper method to get user by Email
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomException("User not found with email: " + email));
	}

	// Get User by ID (Admin)
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new CustomException("User not found with ID: " + id));
	}

	// Profile Update (Customer)
	public User updateProfile(String email, User updatedUser) {
		User existingUser = getUserByEmail(email);
		existingUser.setName(updatedUser.getName());
		// Intentionally NOT updating email, password, or role here for security!
		return userRepository.save(existingUser);
	}

	// Change Password (Customer)
	public void changePassword(String email, String newPassword, PasswordEncoder passwordEncoder) {
		User existingUser = getUserByEmail(email);
		existingUser.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(existingUser);
	}

	// Update User (Admin)
	public User updateUserByAdmin(Long id, User updatedUser) {
		User existingUser = getUserById(id);
		existingUser.setName(updatedUser.getName());
		existingUser.setEmail(updatedUser.getEmail());
		if (updatedUser.getRole() != null) {
			existingUser.setRole(updatedUser.getRole());
		}
		return userRepository.save(existingUser);
	}

	// Delete User (Admin)
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}