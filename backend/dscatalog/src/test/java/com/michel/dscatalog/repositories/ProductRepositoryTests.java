package com.michel.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.michel.dscatalog.entities.Product;
import com.michel.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = productRepository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct + 1, product.getId());
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductPresentWhenIdExists() {
		Optional<Product> result = productRepository.findById(existingId);	
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductEmptyWhenIdExists() {
		Optional<Product> result = productRepository.findById(nonExistingId);	
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		productRepository.deleteById(existingId);
		
		Optional<Product> result = productRepository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
		
	}	
}
