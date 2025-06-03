package com.matheusmaciel.comissio.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceUpdateRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.*;
import com.matheusmaciel.comissio.core.domain.repository.*;
import com.matheusmaciel.comissio.core.domain.service.PerformedServiceService;
import com.matheusmaciel.comissio.infra.exception.performedService.BusinessRuleException;
import com.matheusmaciel.comissio.infra.exception.performedService.CommissionRuleNotFoundException;
import com.matheusmaciel.comissio.infra.exception.performedService.UpdatePerformedServiceException;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformedServiceServiceTest {

    @Mock
    private PerformedServiceRepository performedServiceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ServiceTypeRepository serviceTypeRepository;

    @Mock
    private EmployeeComissionRepository employeeComissionRepository;

    @Mock
    private ComissionConfigRepository comissionConfigRepository;

    @InjectMocks
    private PerformedServiceService performedServiceService;

    @Mock
    private ComissionPaymentRepository comissionPaymentRepository; // Adicionar este mock

    private Employee sampleEmployee;
    private User sampleUser;
    private ServiceType sampleServiceType;
    private UUID existingPerformedServiceId;
    private UUID nonExistingId;
    private PerformedService existingPerformedService;

    @BeforeEach
    void setUp(){
        existingPerformedServiceId = UUID.randomUUID();
        nonExistingId = UUID.randomUUID();

        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .email("test")
                .password("test")
                .build();

        sampleEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .user(sampleUser)
                .build();

        sampleServiceType = ServiceType.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .build();

        existingPerformedService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .employee(sampleEmployee)
                .serviceTypeId(sampleServiceType)
                .price(new BigDecimal("100.00"))
                .comissionAmount(new BigDecimal("10.00"))
                .serviceDate(LocalDate.of(2025, 1, 15))
                .status(ServiceStatus.COMMISSION_PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Mockito.reset(performedServiceRepository, employeeRepository,
                serviceTypeRepository, employeeComissionRepository,
                comissionConfigRepository, comissionPaymentRepository);
    }

    @Test
    @DisplayName("getPerformedServiceById should return DTO when ID exists")
    void getPerformedServiceById_whenIdExists_shouldReturnDto() {
        PerformedService foundService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .employee(sampleEmployee)
                .serviceTypeId(sampleServiceType)
                .price(new BigDecimal("100"))
                .comissionAmount(BigDecimal.TEN)
                .serviceDate(LocalDate.now())
                .status(ServiceStatus.COMMISSION_PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(foundService));

        PerformedServiceResponseDTO result = performedServiceService.getPerformedServiceById(existingPerformedServiceId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(existingPerformedServiceId);
        verify(performedServiceRepository).findById(existingPerformedServiceId);

    }

    @Test
    @DisplayName("getPerformedServiceById should throw ResourceNotFoundException when ID does not exist")
    void getPerformedServiceById_whenIdDoesNotExist_shouldThrowException() {
        when(performedServiceRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.getPerformedServiceById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PerformedService not found with ID: " + nonExistingId);
    }

    @Test
    @DisplayName("cancelPerformedService should throw BusinessRuleException when status is paid")
    void cancelPerformedService_whenStatusIsPaid_shouldThrowBusinessRuleException() {
        PerformedService paidService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .status(ServiceStatus.COMMISSION_PAID)
                .employee(sampleEmployee)
                .serviceTypeId(sampleServiceType)
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(paidService));

        assertThatThrownBy(() -> performedServiceService.cancelPerformedService(existingPerformedServiceId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Performed Service cannot be cancelled. Status is not COMMISSION_PENDING. Current status: COMMISSION_PAID");

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("cancelPerformedService should throw BusinessRuleException when status is already cancelled")
    void cancelPerformedService_whenStatusIsAlreadyCancelled_shouldThrowBusinessRuleException() {
        PerformedService alreadyCancelledService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .status(ServiceStatus.CANCELLED)
                .employee(sampleEmployee)
                .serviceTypeId(sampleServiceType)
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(alreadyCancelledService));

        assertThatThrownBy(() -> performedServiceService.cancelPerformedService(existingPerformedServiceId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Performed Service cannot be cancelled. Status is not COMMISSION_PENDING. Current status: " + ServiceStatus.CANCELLED);

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("updatePerformedService should update price, date and recalculate comission when price changes")
    void updatePerformedService_whenPriceChanges_shouldUpdateAndRecalculate() {
        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(
                new BigDecimal("120.00"), // Novo preço
                LocalDate.of(2025, 1, 20)  // Nova data
        );


        PerformedService serviceFromDb = PerformedService.builder()
                .id(existingPerformedService.getId())
                .employee(existingPerformedService.getEmployee())
                .serviceTypeId(existingPerformedService.getServiceTypeId())
                .price(existingPerformedService.getPrice())
                .comissionAmount(existingPerformedService.getComissionAmount())
                .serviceDate(existingPerformedService.getServiceDate())
                .status(ServiceStatus.COMMISSION_PENDING)
                .createdAt(existingPerformedService.getCreatedAt())
                .updatedAt(existingPerformedService.getUpdatedAt())
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(serviceFromDb));

        when(employeeComissionRepository.findByEmployeeIdAndServiceTypeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        ComissionConfig defaultConfig = ComissionConfig.builder().defaultPercentage(new BigDecimal("10.00")).build(); // 10%
        when(comissionConfigRepository.findByServiceTypeId(any(UUID.class)))
                .thenReturn(Optional.of(defaultConfig));

        when(performedServiceRepository.save(any(PerformedService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PerformedServiceResponseDTO result = performedServiceService.updatePerformedService(existingPerformedServiceId, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualByComparingTo("120.00");
        assertThat(result.serviceDate()).isEqualTo(LocalDate.of(2025, 1, 20));
        // New comission: 120.00 * 10% = 12.00
        assertThat(result.comissionAmount()).isEqualByComparingTo("12.00");
        assertThat(result.status()).isEqualTo(ServiceStatus.COMMISSION_PENDING);

        ArgumentCaptor<PerformedService> serviceCaptor = ArgumentCaptor.forClass(PerformedService.class);
        verify(performedServiceRepository).save(serviceCaptor.capture());
        PerformedService savedService = serviceCaptor.getValue();
        assertThat(savedService.getPrice()).isEqualByComparingTo("120.00");
        assertThat(savedService.getServiceDate()).isEqualTo(LocalDate.of(2025, 1, 20));
        assertThat(savedService.getComissionAmount()).isEqualByComparingTo("12.00");
    }

    @Test
    @DisplayName("updatePerformedService should update only date when price is same or null in DTO (no recalculation)")
    void updatePerformedService_whenOnlyDateChanges_shouldUpdateDateAndNotRecalculate() {

        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(
                existingPerformedService.getPrice(), // Same price
                LocalDate.of(2025, 1, 25)
        );

        PerformedService serviceFromDb = PerformedService.builder()
                .id(existingPerformedService.getId())
                .employee(existingPerformedService.getEmployee())
                .serviceTypeId(existingPerformedService.getServiceTypeId())
                .price(existingPerformedService.getPrice())
                .comissionAmount(existingPerformedService.getComissionAmount()) // Original comission
                .serviceDate(existingPerformedService.getServiceDate())
                .status(ServiceStatus.COMMISSION_PENDING)
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(serviceFromDb));
        when(performedServiceRepository.save(any(PerformedService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PerformedServiceResponseDTO result = performedServiceService.updatePerformedService(existingPerformedServiceId, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualByComparingTo(existingPerformedService.getPrice()); // Price has not changed
        assertThat(result.serviceDate()).isEqualTo(LocalDate.of(2025, 1, 25));
        assertThat(result.comissionAmount()).isEqualByComparingTo(existingPerformedService.getComissionAmount()); // Comission has not changed

        verify(employeeComissionRepository, never()).findByEmployeeIdAndServiceTypeId(any(), any()); // Not should recalculate
        verify(comissionConfigRepository, never()).findByServiceTypeId(any()); // Not should recalculate
        verify(performedServiceRepository).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("updatePerformedService should throw ResourceNotFoundException when ID does not exist")
    void updatePerformedService_whenIdDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(new BigDecimal("100"), LocalDate.now());
        when(performedServiceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.updatePerformedService(nonExistentId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PerformedService not found with ID: " + nonExistentId);
    }

    @Test
    @DisplayName("updatePerformedService should throw UpdatePerformedServiceException when service is COMMISSION_PAID")
    void updatePerformedService_whenServiceIsPaid_shouldThrowUpdateException() {

        existingPerformedService.setStatus(ServiceStatus.COMMISSION_PAID);
        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(new BigDecimal("100"), LocalDate.now());
        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(existingPerformedService));

        assertThatThrownBy(() -> performedServiceService.updatePerformedService(existingPerformedServiceId, updateDTO))
                .isInstanceOf(UpdatePerformedServiceException.class)
                .hasMessageContaining("cannot update an order that has already been paid or cancelled. Current status: " + ServiceStatus.COMMISSION_PAID);
    }

    @Test
    @DisplayName("updatePerformedService should throw UpdatePerformedServiceException when service is CANCELLED")
    void updatePerformedService_whenServiceIsCancelled_shouldThrowUpdateException() {

        existingPerformedService.setStatus(ServiceStatus.CANCELLED); // Altera o status
        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(new BigDecimal("100"), LocalDate.now());
        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(existingPerformedService));

        assertThatThrownBy(() -> performedServiceService.updatePerformedService(existingPerformedServiceId, updateDTO))
                .isInstanceOf(UpdatePerformedServiceException.class)
                .hasMessageContaining("cannot update an order that has already been paid or cancelled. Current status: " + ServiceStatus.CANCELLED);
    }

    @Test
    @DisplayName("updatePerformedService should throw CommissionRuleNotFoundException if no rule found during recalculation")
    void updatePerformedService_whenPriceChangesAndNoRule_shouldThrowCommissionRuleNotFoundException() {
        PerformedServiceUpdateRequestDTO updateDTO = new PerformedServiceUpdateRequestDTO(
                new BigDecimal("120.00"),
                LocalDate.of(2025, 1, 20)
        );
        PerformedService serviceFromDb = PerformedService.builder()
                .id(existingPerformedService.getId())
                .employee(existingPerformedService.getEmployee())
                .serviceTypeId(existingPerformedService.getServiceTypeId())
                .price(new BigDecimal("100.00")) // Original price
                .comissionAmount(new BigDecimal("10.00"))
                .serviceDate(LocalDate.of(2025, 1, 15))
                .status(ServiceStatus.COMMISSION_PENDING)
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(serviceFromDb));

        when(employeeComissionRepository.findByEmployeeIdAndServiceTypeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        when(comissionConfigRepository.findByServiceTypeId(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.updatePerformedService(existingPerformedServiceId, updateDTO))
                .isInstanceOf(CommissionRuleNotFoundException.class)
                .hasMessageContaining("Commission rule not found");

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("createPerformedService should register service and use default commission when no specific rule exists")
    void createPerformedService_whenNoSpecificRule_shouldUseDefaultCommission() {

        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                sampleEmployee.getId(),
                sampleServiceType.getId(),
                new BigDecimal("200.00"),
                LocalDate.of(2025, 6, 1)
        );


        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));
        when(serviceTypeRepository.findById(sampleServiceType.getId())).thenReturn(Optional.of(sampleServiceType));


        when(employeeComissionRepository.findByEmployeeIdAndServiceTypeId(sampleEmployee.getId(), sampleServiceType.getId()))
                .thenReturn(Optional.empty());

        ComissionConfig defaultComissionConfig = ComissionConfig.builder()
                .id(UUID.randomUUID())
                .serviceType(sampleServiceType)
                .defaultPercentage(new BigDecimal("10.00")) // 10%
                .build();
        when(comissionConfigRepository.findByServiceTypeId(sampleServiceType.getId()))
                .thenReturn(Optional.of(defaultComissionConfig));

        when(performedServiceRepository.save(any(PerformedService.class)))
                .thenAnswer(invocation -> {
                    PerformedService serviceToSave = invocation.getArgument(0);
                    if (serviceToSave.getId() == null) {
                        serviceToSave.setId(UUID.randomUUID());
                    }
                    serviceToSave.setCreatedAt(LocalDateTime.now());
                    serviceToSave.setUpdatedAt(LocalDateTime.now());
                    return serviceToSave;
                });

        PerformedServiceResponseDTO result = performedServiceService.createPerformedService(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.employeeId()).isEqualTo(sampleEmployee.getId());
        assertThat(result.serviceTypeId()).isEqualTo(sampleServiceType.getId());
        assertThat(result.comissionAmount()).isEqualByComparingTo("20.00");

        ArgumentCaptor<PerformedService> performedServiceCaptor = ArgumentCaptor.forClass(PerformedService.class);
        verify(performedServiceRepository).save(performedServiceCaptor.capture());
        PerformedService capturedService = performedServiceCaptor.getValue();

        assertThat(capturedService.getEmployee().getId()).isEqualTo(sampleEmployee.getId());
        assertThat(capturedService.getServiceTypeId().getId()).isEqualTo(sampleServiceType.getId());
    }


    @Test
    @DisplayName("createPerformedService should use employee-specific commission when it exists, overriding default")
    void createPerformedService_whenSpecificRuleExists_shouldUseEmployeeSpecificCommission() {

        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                sampleEmployee.getId(),
                sampleServiceType.getId(),
                new BigDecimal("400.00"),
                LocalDate.of(2025, 7, 10)
        );

        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));
        when(serviceTypeRepository.findById(sampleServiceType.getId())).thenReturn(Optional.of(sampleServiceType));

        BigDecimal specificPercentage = new BigDecimal("12.50"); // 12.5%
        EmployeeComission specificEmployeeComission = EmployeeComission.builder()
                .id(UUID.randomUUID())
                .employee(sampleEmployee)
                .serviceType(sampleServiceType)
                .customPercentage(specificPercentage)
                .build();
        when(employeeComissionRepository.findByEmployeeIdAndServiceTypeId(sampleEmployee.getId(), sampleServiceType.getId()))
                .thenReturn(Optional.of(specificEmployeeComission));

        when(performedServiceRepository.save(any(PerformedService.class)))
                .thenAnswer(invocation -> {
                    PerformedService serviceToSave = invocation.getArgument(0);
                    if (serviceToSave.getId() == null) {
                        serviceToSave.setId(UUID.randomUUID());
                    }
                    serviceToSave.setCreatedAt(LocalDateTime.now());
                    serviceToSave.setUpdatedAt(LocalDateTime.now());
                    return serviceToSave;
                });

        PerformedServiceResponseDTO result = performedServiceService.createPerformedService(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.employeeId()).isEqualTo(sampleEmployee.getId());
        assertThat(result.serviceTypeId()).isEqualTo(sampleServiceType.getId());
        assertThat(result.price()).isEqualByComparingTo("400.00");
        assertThat(result.serviceDate()).isEqualTo(LocalDate.of(2025, 7, 10));
        assertThat(result.status()).isEqualTo(ServiceStatus.COMMISSION_PENDING);

        // 12.5% de 400.00 = 50.00
        // 400.00 * (12.50 / 100) = 400.00 * 0.1250 = 50.00
        assertThat(result.comissionAmount()).isEqualByComparingTo("50.00");

        ArgumentCaptor<PerformedService> performedServiceCaptor = ArgumentCaptor.forClass(PerformedService.class);
        verify(performedServiceRepository).save(performedServiceCaptor.capture());
        PerformedService capturedService = performedServiceCaptor.getValue();

        assertThat(capturedService.getEmployee().getId()).isEqualTo(sampleEmployee.getId());
        assertThat(capturedService.getServiceTypeId().getId()).isEqualTo(sampleServiceType.getId());
        assertThat(capturedService.getComissionAmount()).isEqualByComparingTo("50.00");

        verify(comissionConfigRepository, never()).findByServiceTypeId(any(UUID.class));
    }

    @Test
    @DisplayName("createPerformedService should throw ResourceNotFoundException when Employee is not found")
    void createPerformedService_whenEmployeeNotFound_shouldThrowResourceNotFoundException() {

        UUID nonExistentEmployeeId = UUID.randomUUID();
        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                nonExistentEmployeeId,
                sampleServiceType.getId(),
                new BigDecimal("100.00"),
                LocalDate.now()
        );

        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.createPerformedService(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: " + nonExistentEmployeeId);

        verify(serviceTypeRepository, never()).findById(any(UUID.class));
        verify(employeeComissionRepository, never()).findByEmployeeIdAndServiceTypeId(any(UUID.class), any(UUID.class));
        verify(comissionConfigRepository, never()).findByServiceTypeId(any(UUID.class));
        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("createPerformedService should throw ResourceNotFoundException when ServiceType is not found")
    void createPerformedService_whenServiceTypeNotFound_shouldThrowResourceNotFoundException() {

        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                sampleEmployee.getId(),
                nonExistingId,
                new BigDecimal("100.00"),
                LocalDate.now()
        );

        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));
        when(serviceTypeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.createPerformedService(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ServiceType not found with ID: " + nonExistingId);

        verify(employeeComissionRepository, never()).findByEmployeeIdAndServiceTypeId(any(UUID.class), any(UUID.class));
        verify(comissionConfigRepository, never()).findByServiceTypeId(any(UUID.class));
        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }

    @Test
    @DisplayName("createPerformedService should throw CommissionRuleNotFoundException when no commission rule is found")
    void createPerformedService_whenNoCommissionRuleFound_shouldThrowCommissionRuleNotFoundException() {

        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                sampleEmployee.getId(),
                sampleServiceType.getId(),
                new BigDecimal("100.00"),
                LocalDate.now()
        );

        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));
        when(serviceTypeRepository.findById(sampleServiceType.getId())).thenReturn(Optional.of(sampleServiceType));

        when(employeeComissionRepository.findByEmployeeIdAndServiceTypeId(sampleEmployee.getId(), sampleServiceType.getId()))
                .thenReturn(Optional.empty());
        when(comissionConfigRepository.findByServiceTypeId(sampleServiceType.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.createPerformedService(requestDTO))
                .isInstanceOf(CommissionRuleNotFoundException.class)
                .hasMessageContaining("Commission rule not found for Employee: " + sampleEmployee.getId() +
                        " and ServiceType: " + sampleServiceType.getId());

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
    }


    @Test
    @DisplayName("markCommissionAsPaid should update PerformedService status and create ComissionPayment when status is COMMISSION_PENDING")
    void markCommissionAsPaid_whenStatusIsPending_shouldSucceed() {

        BigDecimal expectedComissionAmount = new BigDecimal("15.75");
        PerformedService pendingService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .employee(sampleEmployee)
                .serviceTypeId(sampleServiceType)
                .comissionAmount(expectedComissionAmount)
                .status(ServiceStatus.COMMISSION_PENDING)
                .build();

        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(pendingService));

        when(performedServiceRepository.save(any(PerformedService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(comissionPaymentRepository.save(any(ComissionPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PerformedServiceResponseDTO result = performedServiceService.markCommissionAsPaid(existingPerformedServiceId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(ServiceStatus.COMMISSION_PAID);

        ArgumentCaptor<PerformedService> performedServiceCaptor = ArgumentCaptor.forClass(PerformedService.class);
        verify(performedServiceRepository).save(performedServiceCaptor.capture());
        assertThat(performedServiceCaptor.getValue().getStatus()).isEqualTo(ServiceStatus.COMMISSION_PAID);

        ArgumentCaptor<ComissionPayment> comissionPaymentCaptor = ArgumentCaptor.forClass(ComissionPayment.class);
        verify(comissionPaymentRepository).save(comissionPaymentCaptor.capture());
        ComissionPayment savedPayment = comissionPaymentCaptor.getValue();

        assertThat(savedPayment.getEmployee().getId()).isEqualTo(sampleEmployee.getId());
        assertThat(savedPayment.getPerformedService().getId()).isEqualTo(existingPerformedServiceId);
        assertThat(savedPayment.getAmountPaid()).isEqualByComparingTo(expectedComissionAmount);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(savedPayment.getPaymentDate()).isNotNull();
    }

    @Test
    @DisplayName("markCommissionAsPaid should throw ResourceNotFoundException when PerformedService ID does not exist")
    void markCommissionAsPaid_whenServiceNotFound_shouldThrowResourceNotFoundException() {
        when(performedServiceRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> performedServiceService.markCommissionAsPaid(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PerformedService not found with ID: " + nonExistingId);

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
        verify(comissionPaymentRepository, never()).save(any(ComissionPayment.class));
    }

    @Test
    @DisplayName("markCommissionAsPaid should throw BusinessRuleException when PerformedService status is already COMMISSION_PAID")
    void markCommissionAsPaid_whenAlreadyPaid_shouldThrowBusinessRuleException() {
        PerformedService alreadyPaidService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .status(ServiceStatus.COMMISSION_PAID) // Já está pago
                .employee(sampleEmployee)
                .build();
        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(alreadyPaidService));

        assertThatThrownBy(() -> performedServiceService.markCommissionAsPaid(existingPerformedServiceId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot mark commission as paid. Service status is not COMMISSION_PENDING. Current status: " + ServiceStatus.COMMISSION_PAID);

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
        verify(comissionPaymentRepository, never()).save(any(ComissionPayment.class));
    }

    @Test
    @DisplayName("markCommissionAsPaid should throw BusinessRuleException when PerformedService status is CANCELLED")
    void markCommissionAsPaid_whenCancelled_shouldThrowBusinessRuleException() {

        PerformedService cancelledService = PerformedService.builder()
                .id(existingPerformedServiceId)
                .status(ServiceStatus.CANCELLED)
                .employee(sampleEmployee)
                .build();
        when(performedServiceRepository.findById(existingPerformedServiceId)).thenReturn(Optional.of(cancelledService));

        assertThatThrownBy(() -> performedServiceService.markCommissionAsPaid(existingPerformedServiceId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot mark commission as paid. Service status is not COMMISSION_PENDING. Current status: " + ServiceStatus.CANCELLED);

        verify(performedServiceRepository, never()).save(any(PerformedService.class));
        verify(comissionPaymentRepository, never()).save(any(ComissionPayment.class));
    }

}



