package com.michel.dscatalog.services;

import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.michel.dscatalog.entities.Category;
import com.michel.dscatalog.entities.Product;
import com.michel.dscatalog.entities.dto.category.CategoryDTO;
import com.michel.dscatalog.entities.dto.product.ProductDTO;
import com.michel.dscatalog.repositories.CategoryRepository;
import com.michel.dscatalog.repositories.ProductRepository;
import com.michel.dscatalog.services.exception.DatabaseException;
import com.michel.dscatalog.services.exception.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable){
		Page<Product> listProductDto = productRepository.findAll(pageable);
		return listProductDto.map(element -> new ProductDTO(element));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDto) {
		Product productEntity = new Product();
		copyDtoToEntity(productDto, productEntity);
		productEntity = productRepository.save(productEntity);
		
		return new ProductDTO(productEntity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {
			Product productEntity = productRepository.getReferenceById(id);
			copyDtoToEntity(productDto, productEntity);
			productEntity = productRepository.save(productEntity);
			return new ProductDTO(productEntity);
		} catch (EntityActionVetoException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}		
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteById(Long id) {
		if(!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
		try {
			productRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO productDto, Product productEntity) {
		productEntity.setName(productDto.getName());
		productEntity.setDescription(productDto.getDescription());
		productEntity.setPrice(productDto.getPrice());
		productEntity.setImgUrl(productDto.getImgUrl());
		productEntity.setDate(productDto.getDate());
		
		productEntity.getCategories().clear();
		for (CategoryDTO oneCategoryDto : productDto.getCategories()) {
			Category category = categoryRepository.getReferenceById(oneCategoryDto.getId());
			productEntity.getCategories().add(category);
		}
	}

}
