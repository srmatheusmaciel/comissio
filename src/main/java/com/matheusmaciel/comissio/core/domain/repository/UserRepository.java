package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameOrEmail(String username, String email);
}
