package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody Employee employee) {
        try {
            // Verificar se o 'User' foi corretamente atribuído ao 'Employee'
            if (employee.getUser() == null || employee.getUser().getId() == null) {
                return ResponseEntity.badRequest().body("User ID cannot be null.");
            }

            var result = this.employeeService.execute(employee);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
