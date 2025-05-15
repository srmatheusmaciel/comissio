package com.matheusmaciel.comissio.core.domain.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}