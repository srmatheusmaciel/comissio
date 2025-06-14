package com.matheusmaciel.comissio.core.dto.comissionConfig;

import com.matheusmaciel.comissio.core.model.register.ComissionConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ComissionConfigResponseDTO(
    UUID id,
    UUID serviceTypeId,
    String serviceTypeName,
    BigDecimal defaultPercentage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ComissionConfigResponseDTO fromEntity(ComissionConfig entity) {
        return new ComissionConfigResponseDTO(
                entity.getId(),
                entity.getServiceType().getId(),
                entity.getServiceType().getName(),
                entity.getDefaultPercentage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
