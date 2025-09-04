package com.devsuperior.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.services.CategoryService;
import com.devsuperior.dscommerce.tests.CategoryFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CategoryService service;

	private List<CategoryDTO> listCategories;
	private Category category;
	private CategoryDTO categoryDTO;

	@BeforeEach
	void setUp() {
		category = CategoryFactory.createCategory(2L, "Home products");
		categoryDTO = new CategoryDTO(category);

		listCategories = List.of(categoryDTO);

		Mockito.when(service.findAll()).thenReturn(listCategories);
	}

	@Test
	public void findAllShouldReturnListOfCategoryDTO() throws Exception {

		ResultActions result = 
				mockMvc.perform(get("/categories")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$[0].id").exists());
		result.andExpect(jsonPath("$[0].id").value(2L));
		result.andExpect(jsonPath("$[0].name").exists());
		result.andExpect(jsonPath("$[0].name").value("Home products"));
	}

}
