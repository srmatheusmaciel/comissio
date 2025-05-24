package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeUpdateRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeRepository;
import com.matheusmaciel.comissio.infra.exception.employee.EmployeeFoundException;
import com.matheusmaciel.comissio.infra.exception.employee.EmployeeNotFoundException;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public EmployeeService(EmployeeRepository employeeRepository, UserService userService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    @Transactional
    public EmployeeResponseDTO registerEmployee(EmployeeRequestDTO dto) {


        if (employeeRepository.existsByUser_Id(dto.userId())) {
            throw new EmployeeFoundException();
        }

        User user = this.userService.findById(dto.userId());


        Employee employee = Employee.builder()
                .user(user)
                .status(dto.status())
                .build();


        Employee savedEmployee = this.employeeRepository.save(employee);

        return EmployeeResponseDTO.fromEntity(savedEmployee);
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return this.employeeRepository.findAll().stream().map(EmployeeResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(UUID employeeId, EmployeeUpdateRequestDTO dto) {
        Employee employee = this.employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        employee.setStatus(dto.status());

        Employee updatedEmployee = this.employeeRepository.save(employee);
        return EmployeeResponseDTO.fromEntity(updatedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO getEmployeeById(UUID employeeId) {
        Employee employee = this.employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return EmployeeResponseDTO.fromEntity(employee);
    }
}
