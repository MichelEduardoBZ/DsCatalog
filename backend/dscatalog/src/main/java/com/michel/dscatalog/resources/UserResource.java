package com.michel.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.michel.dscatalog.entities.dto.security.UserDTO;
import com.michel.dscatalog.entities.dto.security.UserInsertDTO;
import com.michel.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
		Page<UserDTO> list = userService.findAllPaged(pageable);
		 return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id){
		 return ResponseEntity.ok(userService.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO productDTO){
		UserDTO newProductDTO = userService.insert(productDTO);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newProductDTO.getId()).toUri();
		return ResponseEntity.created(uri).body(newProductDTO);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO productDTO){
		productDTO = userService.update(id, productDTO);
		return ResponseEntity.ok().body(productDTO);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id){
		userService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
