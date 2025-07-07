package com.matheusmaciel.comissio.core.service;

import com.matheusmaciel.comissio.core.model.access.User;
import com.matheusmaciel.comissio.core.repository.UserRepository;
import com.matheusmaciel.comissio.infra.exception.BusinessException;
import com.matheusmaciel.comissio.infra.exception.user.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public void generatePasswordResetToken(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

            user.setPasswordResetToken(token);
            user.setPasswordResetTokenExpiry(expiryDate);

            userRepository.save(user);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
        });
        
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token de recuperação inválido ou não encontrado."));

        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            userRepository.save(user);
            throw new BusinessException("Token de recuperação expirado.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);
    }
}
