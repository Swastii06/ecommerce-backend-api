package com.incture.ecommerceBackend.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailService emailService;

	@DisplayName("Test Send Order Confirmation - Success")
	@Test
	void testSendOrderConfirmation_Success() {
		// Act
		emailService.sendOrderConfirmation("customer@example.com", 123L, "5000.00");

		// Assert: Verify that mailSender.send() was called exactly once with any
		// message
		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	@DisplayName("Test Send Order Confirmation - Exception Handling")
	@Test
	void testSendOrderConfirmation_Failure() {
		// Arrange: Force the mailSender to throw an exception when called
		doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

		// Act: This should not crash the app because of the try-catch block in
		// EmailService
		emailService.sendOrderConfirmation("customer@example.com", 123L, "5000.00");

		// Assert: Verify that the send attempt was still made
		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
		// The test passes if no exception escapes the service method
	}
}