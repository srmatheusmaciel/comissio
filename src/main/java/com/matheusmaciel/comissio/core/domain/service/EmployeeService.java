package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public EmployeeService(EmployeeRepository employeeRepository, UserService userService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    public Employee execute(Employee employee) {
        if (employee.getUser() == null || employee.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        User user = this.userService.findById(employee.getUser().getId());
        employee.setUser(user);

        return this.employeeRepository.save(employee);
    }

}
