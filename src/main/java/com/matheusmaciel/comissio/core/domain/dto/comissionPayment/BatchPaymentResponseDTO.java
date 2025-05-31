package com.matheusmaciel.comissio.core.domain.dto.comissionPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BatchPaymentResponseDTO(
        UUID employeeId,
        String employeeName,
        int commissionsPaidCount,
        BigDecimal totalPaid,
        LocalDateTime batchProcessTime,
        List<UUID> paidPerformedServiceIds
) {
}
