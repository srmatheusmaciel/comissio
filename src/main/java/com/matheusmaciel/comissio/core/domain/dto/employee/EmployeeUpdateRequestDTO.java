package com.matheusmaciel.comissio.core.domain.dto.employee;

import com.matheusmaciel.comissio.core.domain.model.register.StatusEmployee;

import jakarta.validation.constraints.NotNull;

public record EmployeeUpdateRequestDTO(@NotNull StatusEmployee status) {

}
