package com.matheusmaciel.comissio.core.domain.service;

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

    public Employee execute(Employee employee) {
        UUID userId = Optional.ofNullable(employee.getUser())
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User ID cannot be null."));

        User user = this.userService.findById(userId);
        employee.setUser(user); // garante que virá do banco

        return this.employeeRepository.save(employee);
    }


}
