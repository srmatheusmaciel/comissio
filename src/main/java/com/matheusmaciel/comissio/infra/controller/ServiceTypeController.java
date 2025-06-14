package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.dto.ServiceType.ServiceTypeRequestDTO;
import com.matheusmaciel.comissio.core.dto.ServiceType.ServiceTypeResponseDTO;
import com.matheusmaciel.comissio.core.service.ServiceTypeService;
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
@RequestMapping("/service-types")
@Tag(name = "Service Types", description = "Operations for managing service types")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new service type")
    @ApiResponse(responseCode = "201", description = "Service type created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Service type name already exists")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<ServiceTypeResponseDTO> create(@Valid @RequestBody ServiceTypeRequestDTO dto) {
       ServiceTypeResponseDTO createdServiceType = this.serviceTypeService.createServiceType(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdServiceType.id())
                .toUri();
        return ResponseEntity.created(location).body(createdServiceType);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a service type by ID")
    @ApiResponse(responseCode = "200", description = "Service type found")
    @ApiResponse(responseCode = "404", description = "Service type not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<ServiceTypeResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceTypeService.getServiceTypeById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List all service types")
    @ApiResponse(responseCode = "200", description = "List of service types retrieved")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<ServiceTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(serviceTypeService.getAllServiceTypes());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing service type")
    @ApiResponse(responseCode = "200", description = "Service type updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Service type not found")
    @ApiResponse(responseCode = "409", description = "Service type name already in use by another service")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<ServiceTypeResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ServiceTypeRequestDTO dto) {
        return ResponseEntity.ok(serviceTypeService.updateServiceType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a service type by ID")
    @ApiResponse(responseCode = "204", description = "Service type deleted successfully")
    @ApiResponse(responseCode = "404", description = "Service type not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceTypeService.deleteServiceType(id);
        return ResponseEntity.noContent().build();
    }


}
