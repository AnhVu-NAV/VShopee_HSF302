package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
