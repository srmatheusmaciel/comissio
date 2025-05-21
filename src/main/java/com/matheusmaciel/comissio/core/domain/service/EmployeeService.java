package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public EmployeeService(EmployeeRepository employeeRepository, UserService userService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    public EmployeeResponseDTO registerEmployee(EmployeeRequestDTO dto) {

        User user = this.userService.findById(dto.userId());


        Employee employee = Employee.builder()
                .user(user)
                .status(dto.status())
                .build();


        Employee savedEmployee = this.employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                savedEmployee.getId(),
                savedEmployee.getUser().getId(),
                savedEmployee.getStatus(),
                savedEmployee.getCreated_at(),
                savedEmployee.getUpdated_at()
        );
    }


}
