package com.incture.ecommerceBackend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class); // used to print logs
	private final JavaMailSender mailSender; // built-in Spring Boot tool that connects to Gmail using the password we
												// provided in application.propertie

	@Autowired
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendOrderConfirmation(String toEmail, Long orderId, String totalAmount) { // used to send order
																							// confirmation email
		try {
			SimpleMailMessage message = new SimpleMailMessage(); // this creates email object, where we fill email
																	// details
			message.setFrom("dashswastideepa@gmail.com");
			message.setTo(toEmail);
			message.setSubject("Order Confirmation - Order #" + orderId);
			message.setText("Hello! \n\nThank you for shopping with us! Your order #" + orderId
					+ " has been successfully placed.\n" + "Total Amount Paid: Rs " + totalAmount + "\n\n"
					+ "We will notify you once your items ship!");

			mailSender.send(message); // sends the actual email
			logger.info("Confirmation email successfully sent to {}", toEmail);

		} catch (Exception e) {
			logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
		}
	}
}