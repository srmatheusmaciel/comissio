package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    UserDetails findByUsername(String username);
}
