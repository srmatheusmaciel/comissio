package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.AuthenticationRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.LoginResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.access.UserRole;
import com.matheusmaciel.comissio.core.domain.repository.UserRepository;
import com.matheusmaciel.comissio.infra.exception.UserFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        this.userRepository
                .findByUsernameOrEmail(dto.getUsername(), dto.getEmail())
                .ifPresent(u -> {
                    throw new UserFoundException();
                });

        var user = User.builder()
                .name(dto.getName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.valueOf(dto.getRole().toUpperCase()))
                .build();

        var saved = this.userRepository.save(user);

        return UserResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }




}
