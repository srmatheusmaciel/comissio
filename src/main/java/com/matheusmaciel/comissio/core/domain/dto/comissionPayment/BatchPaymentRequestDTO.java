package com.matheusmaciel.comissio.core.domain.dto.comissionPayment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record BatchPaymentRequestDTO(
        @NotNull(message = "Employee ID cannot be null")
        UUID employeeId,

        LocalDate upToServiceDate
) {
}
