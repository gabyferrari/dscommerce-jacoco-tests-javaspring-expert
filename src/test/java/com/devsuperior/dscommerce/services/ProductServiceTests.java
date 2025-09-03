package com.devsuperior.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId, nonExistId, dependentId;
	private Product product;
	private String productName;
	private PageImpl<Product> page;
	private ProductDTO productDto;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistId = 2L;
		dependentId = 3L;
		productName = "PlayStation 5";
		product = ProductFactory.createProduct(productName);
		productDto = new ProductDTO(product);
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.searchByName(any(), (Pageable)any())).thenReturn(page);
		
		Mockito.when(repository.save(any())).thenReturn(product);
		
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), product.getName());
	}
	
	@Test
	public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistId);
		});
	}
	
	@Test
	public void findAllShouldReturnPagedProductMinDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		
		Page<ProductMinDTO> result = service.findAll(productName, pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getName(), productName);
	}
	
	@Test
	public void insertShouldReturnProductDTO() {
		ProductDTO result = service.insert(productDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), product.getId());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, productDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), productDto.getName());
	}
	
	@Test
	public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistId, productDto);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistId);
		});
	}
	
	@Test
	public void deleteShouldDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
	}

}
