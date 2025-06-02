package com.matheusmaciel.comissio.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.repository.PerformedServiceRepository;
import com.matheusmaciel.comissio.core.domain.service.PerformedServiceService;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PerformedServiceServiceTest {

    @Mock
    private PerformedServiceRepository performedServiceRepository;

    @InjectMocks
    private PerformedServiceService performedServiceService;

    private Employee sampleEmployee;
    private User sampleUser;
    private ServiceType sampleServiceType;
    private UUID existingPerformedServiceId;
    private UUID nonExistingId;

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


}
