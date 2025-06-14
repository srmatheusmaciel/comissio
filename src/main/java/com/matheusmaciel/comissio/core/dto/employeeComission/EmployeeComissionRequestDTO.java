package com.matheusmaciel.comissio.core.dto.employeeComission;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeComissionRequestDTO(
        @NotNull(message = "Employee ID cannot be null")
        UUID employeeId,

        @NotNull(message = "ServiceType ID cannot be null")
        UUID serviceTypeId,

        @NotNull(message = "Custom percentage cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Custom percentage must be non-negative")
        BigDecimal customPercentage
) {
}
