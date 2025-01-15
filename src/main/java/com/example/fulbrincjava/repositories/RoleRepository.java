package com.example.fulbrincjava.repositories;

import com.example.fulbrincjava.entities.Role;
import com.example.fulbrincjava.entities.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}