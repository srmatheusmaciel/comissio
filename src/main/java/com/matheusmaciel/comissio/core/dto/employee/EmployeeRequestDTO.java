package com.matheusmaciel.comissio.core.dto.employee;

import com.matheusmaciel.comissio.core.model.register.StatusEmployee;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


public record EmployeeRequestDTO(
        @NotNull UUID userId,
        @NotNull StatusEmployee status) {
}
