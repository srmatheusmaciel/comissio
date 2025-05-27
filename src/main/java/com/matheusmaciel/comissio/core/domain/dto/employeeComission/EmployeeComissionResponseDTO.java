package com.matheusmaciel.comissio.core.domain.dto.employeeComission;

import com.matheusmaciel.comissio.core.domain.model.register.EmployeeComission;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeComissionResponseDTO(
        UUID id,
        UUID employeeId,
        UUID serviceTypeId,
        String serviceTypeName,
        BigDecimal customPercentage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EmployeeComissionResponseDTO fromEntity(EmployeeComission entity) {
        return new EmployeeComissionResponseDTO(
                entity.getId(),
                entity.getEmployee().getId(),
                entity.getServiceType().getId(),
                entity.getServiceType().getName(),
                entity.getCustomPercentage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
