package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.employeeComission.EmployeeComissionRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.employeeComission.EmployeeComissionResponseDTO;
import com.matheusmaciel.comissio.core.domain.service.EmployeeComissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee-comissions")
@Tag(name = "Employee Comissions", description = "Operations for managing specific comission configurations per employee and service type")
public class EmployeeComissionController {

    private final EmployeeComissionService employeeComissionService;

    public EmployeeComissionController(EmployeeComissionService employeeComissionService) {
        this.employeeComissionService = employeeComissionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a specific commission configuration for an employee")
    @ApiResponse(responseCode = "201", description = "Employee commission created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data or referenced Employee/ServiceType not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<EmployeeComissionResponseDTO> create(
            @Valid @RequestBody EmployeeComissionRequestDTO dto
    ){
        EmployeeComissionResponseDTO createdComission = employeeComissionService.createEmployeeCommission(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdComission.id())
                .toUri();
        return ResponseEntity.created(location).body(createdComission);
    }

    public ResponseEntity<EmployeeComissionResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody EmployeeComissionRequestDTO dto) {
        EmployeeComissionResponseDTO updatedComission = employeeComissionService.updateEmployeeCommission(id, dto);
        return ResponseEntity.ok(updatedComission);
    }


    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all specific comissions for an employee")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<EmployeeComissionResponseDTO>> getByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeComissionService.getComissionsByEmployeeId(employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a specific employee comission by its ID")
    @ApiResponse(responseCode = "404", description = "EmployeeComission not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<EmployeeComissionResponseDTO> getComissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeComissionService.getEmployeeComissionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all specific employee comissions")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<EmployeeComissionResponseDTO>> getAll() {
        return ResponseEntity.ok(employeeComissionService.getAllComissions());
    }

    @GetMapping("/service/{serviceTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all specific employee comissions for a service type")
    @ApiResponse(responseCode = "404", description = "ServiceType not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<EmployeeComissionResponseDTO>> getByServiceTypeId(@PathVariable UUID serviceTypeId) {
        return ResponseEntity.ok(employeeComissionService.getComissionsByServiceTypeId(serviceTypeId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a specific employee comission by ID")
    @ApiResponse(responseCode = "204", description = "Employee comission deleted successfully")
    @ApiResponse(responseCode = "404", description = "Employee comission not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeComissionService.deleteEmployeeComission(id);
        return ResponseEntity.noContent().build();
    }
}
