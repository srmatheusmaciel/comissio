package com.matheusmaciel.comissio.core.domain.dto.performedService;

import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PerformedServiceResponseDTO(
        UUID id,
        UUID employeeId,
        String employeeName,
        UUID serviceTypeId,
        String serviceTypeName,
        BigDecimal price,
        BigDecimal comissionAmount,
        LocalDate serviceDate,
        ServiceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PerformedServiceResponseDTO fromEntity(PerformedService entity) {
        ServiceType serviceTypeObject = entity.getServiceTypeId();

        return new PerformedServiceResponseDTO(
                entity.getId(),
                entity.getEmployee().getId(),
                entity.getEmployee().getUser().getName(),
                serviceTypeObject != null ? serviceTypeObject.getId() : null,
                serviceTypeObject != null ? serviceTypeObject.getName() : null,
                entity.getPrice(),
                entity.getComissionAmount(),
                entity.getServiceDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
