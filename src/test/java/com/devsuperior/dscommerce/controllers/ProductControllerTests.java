package com.devsuperior.dscommerce.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.services.ProductService;
import com.devsuperior.dscommerce.services.UserService;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private ProductService service;
	
	@MockitoBean
	private UserService userService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingProductId, nonExistingProductId;
	private String productName;
	
	private Product product;
	private ProductDTO productDTO;
	private ProductMinDTO productMinDTO;
	private PageImpl<ProductMinDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		
		Mockito.when(userService.loadUserByUsername(clientUsername)).thenReturn(UserFactory.createClientUser());
		Mockito.when(userService.loadUserByUsername(adminUsername)).thenReturn(UserFactory.createAdminUser());
		
		clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		invalidToken = adminToken + "xpto"; // token invalido
		
		existingProductId = 1L;
		nonExistingProductId = 2L;
		
		productName = "PlayStation";
		product = ProductFactory.createProduct(productName);
		productDTO = new ProductDTO(product);
		productMinDTO = new ProductMinDTO(product);
		
		page = new PageImpl<>(List.of(productMinDTO));
		
		Mockito.when(service.findAll(any(), any())).thenReturn(page);
		
		Mockito.when(service.findById(existingProductId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingProductId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.insert(any())).thenReturn(productDTO);
		
		Mockito.when(service.update(eq(existingProductId), any())).thenReturn(productDTO);
		Mockito.when(service.update(eq(nonExistingProductId), any())).thenThrow(ResourceNotFoundException.class);
	}
	
	@Test
	public void findAllShouldReturnPageWhenProductNameIsEmpty() throws Exception {
		
		ResultActions result = 
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON))
						.andDo(MockMvcResultHandlers.print());
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value(productName));
		result.andExpect(jsonPath("$.pageable").exists());
	}
	
	@Test
	public void findAllShouldReturnPageWhenProductNameIsNotEmpty() throws Exception {
		
		ResultActions result = 
				mockMvc.perform(get("/products?name={productName}", productName)
						.accept(MediaType.APPLICATION_JSON))
						.andDo(MockMvcResultHandlers.print());
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value(productName));
		result.andExpect(jsonPath("$.pageable").exists());
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingProductId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.name").value(productName));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.categories").exists());
	}
	
	@Test
	public void findByIdShouldThrowsNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingProductId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnProductDTOCreatedWhenLoggedAsAdmin() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.name").value(productName));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.categories").exists());		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + clientToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isForbidden());		
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + invalidToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isUnauthorized());		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExistAandAdminLogged() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.name").value(productName));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.categories").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingProductId)
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());		
	}
	
	@Test
	public void updateShouldReturnForbiddenWhenIdExistsAndClientLogged() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + clientToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isForbidden());		
	}
	
	@Test
	public void updateShouldReturnForbiddenWhenIdDoesNotExistAndClientLogged() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingProductId)
						.header("Authorization", "Bearer " + clientToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isForbidden());		
	}
	
	@Test
	public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + invalidToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isUnauthorized());		
	}
	
	@Test
	public void updateShouldReturnForbiddenWhenIdDoesNotExistAndInvalidToken() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingProductId)
						.header("Authorization", "Bearer " + invalidToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isUnauthorized());		
	}
}
