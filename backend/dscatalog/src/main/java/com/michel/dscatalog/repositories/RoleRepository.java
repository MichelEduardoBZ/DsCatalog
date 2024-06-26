package com.michel.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.michel.dscatalog.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
