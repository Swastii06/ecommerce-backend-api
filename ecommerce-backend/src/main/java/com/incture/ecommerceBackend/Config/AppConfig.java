package com.incture.ecommerceBackend.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() { // it looks at the User Entity, looks at the UserResponseDTO, sees that they
										// both have variables called name and email, so instantly copies the data
		return new ModelMapper();
	}
}