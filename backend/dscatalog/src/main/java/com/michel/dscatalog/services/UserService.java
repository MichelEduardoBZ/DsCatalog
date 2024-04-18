package com.michel.dscatalog.services;

import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.michel.dscatalog.entities.Role;
import com.michel.dscatalog.entities.User;
import com.michel.dscatalog.entities.dto.security.RoleDTO;
import com.michel.dscatalog.entities.dto.security.UserDTO;
import com.michel.dscatalog.entities.dto.security.UserInsertDTO;
import com.michel.dscatalog.repositories.RoleRepository;
import com.michel.dscatalog.repositories.UserRepository;
import com.michel.dscatalog.services.exception.DatabaseException;
import com.michel.dscatalog.services.exception.ResourceNotFoundException;

@Service
public class UserService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> listUserDto = userRepository.findAll(pageable);
		return listUserDto.map(element -> new UserDTO(element));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO userDto) {
		User userEntity = new User();
		copyDtoToEntity(userDto, userEntity);
		
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userEntity = userRepository.save(userEntity);

		return new UserDTO(userEntity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO userDto) {
		try {
			User userEntity = userRepository.getReferenceById(id);
			copyDtoToEntity(userDto, userEntity);
			userEntity = userRepository.save(userEntity);
			return new UserDTO(userEntity);
		} catch (EntityActionVetoException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found " + id);
		}

		try {
			userRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(UserDTO userDto, User userEntity) {
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userEntity.setEmail(userDto.getEmail());
		
		for (RoleDTO roleDto : userDto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDto.getId());
			userEntity.getRoles().add(role);
		}
	}
}
