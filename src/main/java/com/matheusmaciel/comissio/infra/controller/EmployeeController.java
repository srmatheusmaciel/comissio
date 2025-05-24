package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.UserResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeUpdateRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.repository.EmployeeRepository;
import com.matheusmaciel.comissio.core.domain.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.jersey.JerseyProperties.Servlet;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "Employees", description = "Employees register")
    @Operation(summary = "Register a new employee", 
            description = "This endpoint registers a new employee in the system")
    @ApiResponse(responseCode = "201", description = "Employee successfully registered",
                 content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data (bad request)")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {

        EmployeeResponseDTO response = employeeService.registerEmployee(dto);

        URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_MANAGER')")
    @Tag(name = "Employees", description = "Employees list")
    @Operation(summary = "List all employees", description = "This endpoint lists all employees in the system")
    @ApiResponse(responseCode = "200", description = "List of employees",
    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponseDTO.class))))
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> response = employeeService.getAllEmployees();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_MANAGER')")
    @Tag(name = "Employees", description = "Employees list")
    @Operation(summary = "List employees by id", description = "This endpoint search employees by id in the system")
    @ApiResponse(responseCode = "200", description = "Employee found",
    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponseDTO.class))))
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable UUID id) {
        EmployeeResponseDTO response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Tag(name = "Employees", description = "Employees update status")
    @Operation(summary = "Update status employee", description = "This endpoint update status employee in the system")
    @ApiResponse(responseCode = "200", description = "Employee updated",
    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponseDTO.class))))
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @ApiResponse(responseCode = "400", description = "Invalid input data (bad request)")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
        @PathVariable UUID id,
        @Valid @RequestBody EmployeeUpdateRequestDTO dto) {

        EmployeeResponseDTO response = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(response);

    }

}
