package com.matheusmaciel.comissio.core.domain.dto.performedService;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PerformedServiceUpdateRequestDTO(
        @NotNull(message ="Price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price,

        @NotNull(message = "Date of service cannot be null")
        @PastOrPresent(message = "Date of service must be in the past or present")
        LocalDate serviceDate
) {
}
