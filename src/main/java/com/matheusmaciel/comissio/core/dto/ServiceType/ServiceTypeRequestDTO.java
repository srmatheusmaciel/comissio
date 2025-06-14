package com.matheusmaciel.comissio.core.dto.ServiceType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServiceTypeRequestDTO(@NotBlank(message = "Name cannot be blank")
                                    String name,

                                    @NotNull(message = "Base price cannot be null")
                                    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be positive")
                                    BigDecimal basePrice) {
}
