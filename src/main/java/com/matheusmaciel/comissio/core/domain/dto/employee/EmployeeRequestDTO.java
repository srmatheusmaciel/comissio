package com.matheusmaciel.comissio.core.domain.dto.employee;

import com.matheusmaciel.comissio.core.domain.model.register.StatusEmployee;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


public record EmployeeRequestDTO(
        @NotNull UUID userId,
        @NotNull StatusEmployee status) {
}
