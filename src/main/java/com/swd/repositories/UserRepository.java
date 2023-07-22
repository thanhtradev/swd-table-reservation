package com.swd.repositories;

import com.swd.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
}
