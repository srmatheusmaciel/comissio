package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.repository.UserRepository;
import com.matheusmaciel.comissio.infra.exception.UserFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(User user) {
        this.userRepository
                .findByUsernameOrEmail(user.getUsername(), user.getEmail())
                .ifPresent( u -> {
                    throw new UserFoundException();
                });

        return this.userRepository.save(user);
    }

    public User findById(UUID id) {
        return this.userRepository.findById(id).orElse(null);
    }


}
