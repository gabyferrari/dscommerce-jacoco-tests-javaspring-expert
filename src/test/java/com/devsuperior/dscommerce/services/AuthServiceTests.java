package com.devsuperior.dscommerce.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {
	
	@InjectMocks
	private AuthService authService;
	
	@Mock
	private UserService userService;
	
	private User admin, selfClient, otherClient;
	
	@BeforeEach
	void setUp() throws Exception {
		admin = UserFactory.createAdminUser();
		selfClient = UserFactory.customClientUser(1L, "Bob");
		otherClient = UserFactory.customClientUser(2L, "Ana");
	}
	
	@Test
	public void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {
		Mockito.when(userService.authenticated()).thenReturn(admin);
		
		Long userId = admin.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = selfClient.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherClientLogged() {
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = otherClient.getId();
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
}
