package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.employeeComission.EmployeeComissionRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.employeeComission.EmployeeComissionResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.EmployeeComission;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeComissionRepository;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeRepository;
import com.matheusmaciel.comissio.core.domain.repository.ServiceTypeRepository;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeComissionService {

    private final EmployeeComissionRepository employeeComissionRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public EmployeeComissionService(EmployeeComissionRepository employeeComissionRepository,
                                    EmployeeRepository employeeRepository,
                                    ServiceTypeRepository serviceTypeRepository) {
        this.employeeComissionRepository = employeeComissionRepository;
        this.employeeRepository = employeeRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @Transactional
    public EmployeeComissionResponseDTO createEmployeeCommission(EmployeeComissionRequestDTO dto) {
        validateEmployeeAndServiceType(dto);

        Optional<EmployeeComission> existing = employeeComissionRepository
                .findByEmployeeIdAndServiceTypeId(dto.employeeId(), dto.serviceTypeId());

        if (existing.isPresent()) {
            throw new DataIntegrityViolationException("Commission already exists for this employee and service type.");
        }

        EmployeeComission employeeCommission = buildCommission(dto);
        EmployeeComission saved = employeeComissionRepository.save(employeeCommission);
        return EmployeeComissionResponseDTO.fromEntity(saved);
    }

    @Transactional
    public EmployeeComissionResponseDTO updateEmployeeCommission(UUID id, EmployeeComissionRequestDTO dto) {
        EmployeeComission employeeCommission = employeeComissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee commission not found with ID: " + id));

        validateEmployeeAndServiceType(dto);

        employeeCommission.setEmployee(
                employeeRepository.findById(dto.employeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.employeeId()))
        );

        employeeCommission.setServiceType(
                serviceTypeRepository.findById(dto.serviceTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("ServiceType not found with ID: " + dto.serviceTypeId()))
        );

        employeeCommission.setCustomPercentage(dto.customPercentage());

        EmployeeComission saved = employeeComissionRepository.save(employeeCommission);
        return EmployeeComissionResponseDTO.fromEntity(saved);
    }

    private void validateEmployeeAndServiceType(EmployeeComissionRequestDTO dto) {
        if (!employeeRepository.existsById(dto.employeeId())) {
            throw new ResourceNotFoundException("Employee not found with ID: " + dto.employeeId());
        }
        if (!serviceTypeRepository.existsById(dto.serviceTypeId())) {
            throw new ResourceNotFoundException("ServiceType not found with ID: " + dto.serviceTypeId());
        }
    }

    private EmployeeComission buildCommission(EmployeeComissionRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.employeeId()));
        ServiceType serviceType = serviceTypeRepository.findById(dto.serviceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceType not found with ID: " + dto.serviceTypeId()));

        EmployeeComission commission = new EmployeeComission();
        commission.setEmployee(employee);
        commission.setServiceType(serviceType);
        commission.setCustomPercentage(dto.customPercentage());

        return commission;
    }



    public List<EmployeeComissionResponseDTO> getComissionsByEmployeeId(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        return employeeComissionRepository.findByEmployeeId(employeeId).stream()
                .map(EmployeeComissionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public EmployeeComissionResponseDTO getEmployeeComissionById(UUID id){
        return employeeComissionRepository.findById(id)
                .map(EmployeeComissionResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeComission not found with ID: " + id));
    }

    public List<EmployeeComissionResponseDTO> getComissionsByServiceTypeId(UUID serviceTypeId) {
        if (!serviceTypeRepository.existsById(serviceTypeId)) {
            throw new ResourceNotFoundException("ServiceType not found with ID: " + serviceTypeId);
        }
        return employeeComissionRepository.findByServiceTypeId(serviceTypeId).stream()
                .map(EmployeeComissionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmployeeComissionResponseDTO> getAllComissions() {
        return employeeComissionRepository.findAll().stream()
                .map(EmployeeComissionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEmployeeComission(UUID id) {
        if (!employeeComissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("EmployeeComission not found with ID: " + id);
        }
        employeeComissionRepository.deleteById(id);
    }

    public Optional<BigDecimal> findCustomPercentage(UUID employeeId, UUID serviceTypeId) {
        return employeeComissionRepository.findByEmployeeIdAndServiceTypeId(employeeId, serviceTypeId)
                .map(EmployeeComission::getCustomPercentage);
    }
}
