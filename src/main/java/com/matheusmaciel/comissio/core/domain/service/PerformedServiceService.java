package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceUpdateRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.register.*;
import com.matheusmaciel.comissio.core.domain.repository.*;
import com.matheusmaciel.comissio.infra.exception.performedService.BusinessRuleException;
import com.matheusmaciel.comissio.infra.exception.performedService.CommissionRuleNotFoundException;
import com.matheusmaciel.comissio.infra.exception.performedService.UpdatePerformedServiceException;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PerformedServiceService {

    private final PerformedServiceRepository performedServiceRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeComissionRepository employeeComissionRepository;
    private final ComissionConfigRepository comissionConfigRepository;
    private final ComissionPaymentRepository comissionPaymentRepository;

    public PerformedServiceService(PerformedServiceRepository performedServiceRepository,
                                   EmployeeRepository employeeRepository,
                                   ServiceTypeRepository serviceTypeRepository,
                                   EmployeeComissionRepository employeeComissionRepository,
                                   ComissionConfigRepository comissionConfigRepository,
                                   ComissionPaymentRepository comissionPaymentRepository) {
        this.performedServiceRepository = performedServiceRepository;
        this.employeeRepository = employeeRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.employeeComissionRepository = employeeComissionRepository;
        this.comissionConfigRepository = comissionConfigRepository;
        this.comissionPaymentRepository = comissionPaymentRepository;
    }

    @Transactional
    public PerformedServiceResponseDTO createPerformedService(PerformedServiceRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.employeeId()));

        ServiceType serviceType = serviceTypeRepository.findById(dto.serviceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceType not found with ID: " + dto.serviceTypeId()));

        BigDecimal comissionPercentage = BigDecimal.ZERO;
        boolean ruleFound = false;

        Optional<EmployeeComission> employeeComissionOpt = employeeComissionRepository
                .findByEmployeeIdAndServiceTypeId(employee.getId(), serviceType.getId());

        if(employeeComissionOpt.isPresent()) {
            comissionPercentage = employeeComissionOpt.get().getCustomPercentage();
            ruleFound = true;
        } else {
            Optional<ComissionConfig> comissionConfigOpt = comissionConfigRepository.findByServiceTypeId(serviceType.getId());
            if(comissionConfigOpt.isPresent()) {
                comissionPercentage = comissionConfigOpt.get().getDefaultPercentage();
                ruleFound = true;
            }
        }

        if(!ruleFound) {
            throw new CommissionRuleNotFoundException("Commission rule not found for Employee: " + employee.getId() + " and ServiceType: " + serviceType.getId());
        }

        BigDecimal calculatedAmount = dto.price()
                .multiply(comissionPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        PerformedService performedService = PerformedService.builder()
                .employee(employee)
                .serviceTypeId(serviceType)
                .price(dto.price())
                .comissionAmount(calculatedAmount)
                .status(ServiceStatus.COMMISSION_PENDING)
                .serviceDate(dto.serviceDate())
                .build();

        PerformedService savedService = performedServiceRepository.save(performedService);
        return PerformedServiceResponseDTO.fromEntity(savedService);
    }

    @Transactional
    public PerformedServiceResponseDTO cancelPerformedService(UUID performedServiceId) {
        PerformedService service = performedServiceRepository.findById(performedServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("PerformedService not found with ID: " + performedServiceId));

        if(service.getStatus() != ServiceStatus.COMMISSION_PENDING) {
            throw new BusinessRuleException("Performed Service cannot be cancelled. Status is not COMMISSION_PENDING. Current status: " + service.getStatus());
        }

        service.setStatus(ServiceStatus.CANCELLED);
        service.setComissionAmount(BigDecimal.ZERO);
        PerformedService cancelledService = performedServiceRepository.save(service);
        return PerformedServiceResponseDTO.fromEntity(cancelledService);
    }

    @Transactional
    public PerformedServiceResponseDTO updatePerformedService(UUID id, PerformedServiceUpdateRequestDTO dto) {
        PerformedService service = performedServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PerformedService not found with ID: " + id));

        if(service.getStatus() == ServiceStatus.COMMISSION_PAID || service.getStatus() == ServiceStatus.CANCELLED) {
            throw new UpdatePerformedServiceException("cannot update an order that has already been paid or cancelled. Current status: " + service.getStatus());
        }

        boolean needsReCalculation = false;

        if(dto.price() != null && !service.getPrice().equals(dto.price())) {
            service.setPrice(dto.price());
            needsReCalculation = true;
        }

        if(dto.serviceDate() != null){
            service.setServiceDate(dto.serviceDate());
        }

        if(needsReCalculation){
            BigDecimal comissionPercentage = calculateComissionPercentage(service.getEmployee(), service.getServiceTypeId());
            BigDecimal newCalculatedAmount = service.getPrice()
                    .multiply(comissionPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            service.setComissionAmount(newCalculatedAmount);
        }

        PerformedService updatedService = performedServiceRepository.save(service);
        return PerformedServiceResponseDTO.fromEntity(updatedService);

    }

    private BigDecimal calculateComissionPercentage(Employee employee, ServiceType serviceTypeId) {
        BigDecimal comissionPercentage = BigDecimal.ZERO;
        boolean ruleFound = false;

        Optional<EmployeeComission> employeeComissionOpt = employeeComissionRepository
                .findByEmployeeIdAndServiceTypeId(employee.getId(), serviceTypeId.getId());

        if(employeeComissionOpt.isPresent()) {
            comissionPercentage = employeeComissionOpt.get().getCustomPercentage();
            ruleFound = true;
        } else {
            Optional<ComissionConfig> comissionConfigOpt = comissionConfigRepository.findByServiceTypeId(serviceTypeId.getId());
            if(comissionConfigOpt.isPresent()) {
                comissionPercentage = comissionConfigOpt.get().getDefaultPercentage();
                ruleFound = true;
            }
        }

        if(!ruleFound) {
            throw new CommissionRuleNotFoundException("Commission rule not found for Employee: " + employee.getId()
                    + " and ServiceType: " + serviceTypeId.getId());
        }

        return comissionPercentage;
    }

    @Transactional
    public void deletePerformedService(UUID id) {
        PerformedService performedService = performedServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PerformedService not found with ID: " + id));

        if(performedService.getStatus() == ServiceStatus.COMMISSION_PAID ||
                performedService.getStatus() == ServiceStatus.CANCELLED) {
            throw new UpdatePerformedServiceException("cannot delete an order that has already " +
                    "been paid or cancelled. Current status: " + performedService.getStatus());
        }

        performedServiceRepository.delete(performedService);
    }

    public Page<PerformedServiceResponseDTO> getAllPerformedServices(Pageable pageable) {
        Page<PerformedService> performedServicePage = performedServiceRepository.findAll(pageable);

        return performedServicePage.map(PerformedServiceResponseDTO::fromEntity);
    }

    public Page<PerformedServiceResponseDTO> getAllPerformedServices(
            UUID employeeId,
            ServiceStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable,
            Authentication authentication) {

        User authenticatedUser = (User) authentication.getPrincipal();
        boolean isUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_EMPLOYEE"));

        UUID finalEmployeeId = employeeId;

        if (isUserRole && !authentication.getAuthorities().stream().anyMatch(
                r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_MANAGER"))) {
            Employee employee = employeeRepository.findByUser_Id(authenticatedUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nenhum perfil de funcionário encontrado para o usuário: " + authenticatedUser.getUsername()));
            finalEmployeeId = employee.getId();
        }


        Specification<PerformedService> spec = Specification
                .where(PerformedServiceSpecification.employeeIdEquals(finalEmployeeId))
                .and(PerformedServiceSpecification.statusEquals(status))
                .and(PerformedServiceSpecification.serviceDateGreaterThanOrEquals(startDate))
                .and(PerformedServiceSpecification.serviceDateLessThanOrEquals(endDate));


        Page<PerformedService> performedServicesPage = performedServiceRepository.findAll(spec, pageable);
        return performedServicesPage.map(PerformedServiceResponseDTO::fromEntity);
    }

    public PerformedServiceResponseDTO getPerformedServiceById(UUID id) {
        PerformedService performedService = performedServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PerformedService not found with ID: " + id));
        return PerformedServiceResponseDTO.fromEntity(performedService);
    }

    //payment
    @Transactional
    public PerformedServiceResponseDTO markCommissionAsPaid(UUID performedServiceId) {
        PerformedService performedService = performedServiceRepository.findById(performedServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("PerformedService not found with ID: " + performedServiceId));

        if(performedService.getStatus() != ServiceStatus.COMMISSION_PENDING) {
            throw new BusinessRuleException("Cannot mark commission as paid. Service status is not COMMISSION_PENDING. Current status: " + performedService.getStatus());
        }

        performedService.setStatus(ServiceStatus.COMMISSION_PAID);
        PerformedService updatedPerformedService = performedServiceRepository.save(performedService);


        //Create the CommissionPayment record
        ComissionPayment payment = ComissionPayment.builder()
                .employee(updatedPerformedService.getEmployee())
                .performedService(updatedPerformedService)
                .amountPaid(updatedPerformedService.getComissionAmount())
                .status(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.now())
                .build();
        comissionPaymentRepository.save(payment);

        return PerformedServiceResponseDTO.fromEntity(updatedPerformedService);
    }

}
