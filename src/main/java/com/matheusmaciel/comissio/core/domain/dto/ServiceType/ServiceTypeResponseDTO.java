package com.matheusmaciel.comissio.core.domain.dto.ServiceType;

import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceTypeResponseDTO(UUID id,
                                     String name,
                                     BigDecimal basePrice,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {

    public static ServiceTypeResponseDTO fromEntity(ServiceType entity) {
        return new ServiceTypeResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getBasePrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
