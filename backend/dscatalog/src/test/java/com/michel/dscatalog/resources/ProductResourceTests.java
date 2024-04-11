package com.michel.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michel.dscatalog.entities.dto.product.ProductDTO;
import com.michel.dscatalog.services.ProductService;
import com.michel.dscatalog.services.exception.DatabaseException;
import com.michel.dscatalog.services.exception.ResourceNotFoundException;
import com.michel.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService productService;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;

	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 3L;
		
		productDTO = Factory.createProductDTO();
		
		page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
		Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(productService.update(eq(existingId), any())).thenReturn(productDTO);
		Mockito.when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		Mockito.when(productService.insert(any())).thenReturn(productDTO);
		
		Mockito.doNothing().when(productService).deleteById(existingId);
		Mockito.doThrow(DatabaseException.class).when(productService).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldIdRetornNoContentWhenIdExists() {
		ResultActions result = mockMvc.perform(delete("/prodcts/{id}", existingId))
				.accept(MediaType.APPLICATION_JSON);
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void updateShouldProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(post("/products}")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldNotFoundWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void findAllPageShouldReturnPage() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/products")).andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)).andExpect(status().isNotFound());
	}
	
}
