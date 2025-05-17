package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.AuthenticationRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.LoginResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.access.UserRole;
import com.matheusmaciel.comissio.core.domain.repository.UserRepository;
import com.matheusmaciel.comissio.infra.exception.UserFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }





}
