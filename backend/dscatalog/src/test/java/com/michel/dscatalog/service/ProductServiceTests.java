package com.michel.dscatalog.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.michel.dscatalog.entities.Product;
import com.michel.dscatalog.entities.dto.product.ProductDTO;
import com.michel.dscatalog.repositories.ProductRepository;
import com.michel.dscatalog.services.ProductService;
import com.michel.dscatalog.services.exception.DatabaseException;
import com.michel.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;
	
	@Mock
	private ProductRepository productRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 3L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);	
		
		Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
		Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);
	}
	
	@Test
	public void findAllPageShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = productService.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			productService.deleteById(dependentId);
		});
	}
	
	@Test
	public void deleteShouldIdThrowDatanaseExceptionWhenDependentId() {
		Assertions.assertDoesNotThrow(() -> {
			productService.deleteById(existingId);
		});
	}
}
