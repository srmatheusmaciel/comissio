package com.matheusmaciel.comissio.core.service;

import com.matheusmaciel.comissio.core.dto.comissionPayment.BatchPaymentRequestDTO;
import com.matheusmaciel.comissio.core.dto.comissionPayment.BatchPaymentResponseDTO;
import com.matheusmaciel.comissio.core.dto.comissionPayment.ComissionPaymentResponseDTO;
import com.matheusmaciel.comissio.core.model.register.*;
import com.matheusmaciel.comissio.core.repository.ComissionPaymentRepository;
import com.matheusmaciel.comissio.core.repository.EmployeeRepository;
import com.matheusmaciel.comissio.core.repository.PerformedServiceRepository;
import com.matheusmaciel.comissio.infra.exception.performedService.BusinessRuleException;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ComissionPaymentService {
    private final PerformedServiceRepository performedServiceRepository;
    private final EmployeeRepository employeeRepository;
    private final ComissionPaymentRepository comissionPaymentRepository;

    public ComissionPaymentService(PerformedServiceRepository performedServiceRepository,
                                   EmployeeRepository employeeRepository,
                                   ComissionPaymentRepository comissionPaymentRepository) {
        this.performedServiceRepository = performedServiceRepository;
        this.employeeRepository = employeeRepository;
        this.comissionPaymentRepository = comissionPaymentRepository;
    }

    @Transactional
    public BatchPaymentResponseDTO processBatchPayment(BatchPaymentRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + dto.employeeId()));

        List<PerformedService> servicesToPay;
        if (dto.upToServiceDate() != null) {
            servicesToPay = performedServiceRepository
                    .findByEmployee_IdAndStatusAndServiceDateLessThanEqual(
                            employee.getId(),
                            ServiceStatus.COMMISSION_PENDING,
                            dto.upToServiceDate());
        } else {
            servicesToPay = performedServiceRepository
                    .findByEmployee_IdAndStatus(employee.getId(), ServiceStatus.COMMISSION_PENDING);
        }

        if (servicesToPay.isEmpty()) {
            throw new BusinessRuleException("Nenhuma comissão pendente encontrada para este funcionário" +
                    (dto.upToServiceDate() != null ? " até a data " + dto.upToServiceDate() : "") + ".");
        }

        BigDecimal totalPaidInBatch = BigDecimal.ZERO;
        List<UUID> paidServiceIds = new ArrayList<>();

        for (PerformedService service : servicesToPay) {
            service.setStatus(ServiceStatus.COMMISSION_PAID);
            performedServiceRepository.save(service);

            ComissionPayment payment = ComissionPayment.builder()
                    .employee(employee)
                    .performedService(service)
                    .amountPaid(service.getComissionAmount())
                    .status(PaymentStatus.PAID)
                    .paymentDate(LocalDateTime.now())
                    .build();
            comissionPaymentRepository.save(payment);

            totalPaidInBatch = totalPaidInBatch.add(service.getComissionAmount());
            paidServiceIds.add(service.getId());
        }

        return new BatchPaymentResponseDTO(
                employee.getId(),
                employee.getUser().getName(),
                servicesToPay.size(),
                totalPaidInBatch,
                LocalDateTime.now(),
                paidServiceIds
        );
    }


    public Page<ComissionPaymentResponseDTO> getAllComissionPayments(Pageable pageable) {
        Page<ComissionPayment> comissionPaymentsPage = comissionPaymentRepository.findAll(pageable);
        return comissionPaymentsPage.map(ComissionPaymentResponseDTO::fromEntity);
    }

    public ComissionPaymentResponseDTO getComissionPaymentById(UUID id) {
        ComissionPayment payment = comissionPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with ID: " + id));
        return ComissionPaymentResponseDTO.fromEntity(payment);
    }
}
