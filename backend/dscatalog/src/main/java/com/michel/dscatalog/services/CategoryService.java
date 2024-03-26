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
import com.michel.dscatalog.entities.dto.category.CategoryDTO;
import com.michel.dscatalog.repositories.CategoryRepository;
import com.michel.dscatalog.services.exception.DatabaseException;
import com.michel.dscatalog.services.exception.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page<Category> listCategoryDto = categoryRepository.findAll(pageable);
		return listCategoryDto.map(element -> new CategoryDTO(element));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDto) {
		Category categoryEntity = new Category(categoryDto.getName());
		categoryEntity = categoryRepository.save(categoryEntity);
		
		return new CategoryDTO(categoryEntity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO categoryDto) {
		try {
			Category categoryEntity = categoryRepository.getReferenceById(id);
			categoryEntity.setName(categoryDto.getName());
			categoryEntity = categoryRepository.save(categoryEntity);
			return new CategoryDTO(categoryEntity);
		} catch (EntityActionVetoException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}		
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteById(Long id) {
		if(!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
		try {
			categoryRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
		
	}

}
