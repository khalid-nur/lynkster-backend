package com.lynkster.backend.user.repository;

import com.lynkster.backend.user.entity.Role;
import com.lynkster.backend.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(UserRole name);
}
