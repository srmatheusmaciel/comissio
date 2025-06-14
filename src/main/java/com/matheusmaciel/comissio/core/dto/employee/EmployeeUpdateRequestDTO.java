package com.matheusmaciel.comissio.core.dto.employee;

import com.matheusmaciel.comissio.core.model.register.StatusEmployee;

import jakarta.validation.constraints.NotNull;

public record EmployeeUpdateRequestDTO(@NotNull StatusEmployee status) {

}
