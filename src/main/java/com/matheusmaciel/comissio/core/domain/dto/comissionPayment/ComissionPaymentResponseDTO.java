package com.matheusmaciel.comissio.core.domain.dto.comissionPayment;

import com.matheusmaciel.comissio.core.domain.model.register.ComissionPayment;
import com.matheusmaciel.comissio.core.domain.model.register.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ComissionPaymentResponseDTO(
        UUID id,
        UUID employeeId,
        String employeeName,
        UUID performedServiceId,
        String serviceTypeName,
        BigDecimal amountPaid,
        PaymentStatus status,
        LocalDateTime paymentDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ComissionPaymentResponseDTO fromEntity(ComissionPayment entity) {
        if (entity == null) {
            return null;
        }
        return new ComissionPaymentResponseDTO(
                entity.getId(),
                entity.getEmployee().getId(),
                entity.getEmployee().getUser().getName(),
                entity.getPerformedService().getId(),
                entity.getPerformedService().getServiceTypeId().getName(),
                entity.getAmountPaid(),
                entity.getStatus(),
                entity.getPaymentDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
