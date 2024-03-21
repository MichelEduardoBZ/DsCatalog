package com.michel.dscatalog.entities.dto.category;

import java.io.Serializable;

import com.michel.dscatalog.entities.Category;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	public CategoryDTO() {}
	
	public CategoryDTO(Category entity) {
		this.name = entity.getName();
		this.id = entity.getId();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
