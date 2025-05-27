package com.matheusmaciel.comissio.core.domain.dto.comissionConfig;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ComissionConfigRequestDTO(
        @NotNull(message = "Service type id is required")
        UUID serviceTypeId,

        @NotNull(message = "Default percentage is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Default percentage must be positive")
        BigDecimal defaultPercentage
) {
}
