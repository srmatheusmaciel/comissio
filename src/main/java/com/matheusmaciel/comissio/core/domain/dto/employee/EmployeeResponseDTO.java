package com.matheusmaciel.comissio.core.domain.dto.employee;

import com.matheusmaciel.comissio.core.domain.model.register.StatusEmployee;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponseDTO(
        UUID id,
        UUID userId,
        StatusEmployee status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}