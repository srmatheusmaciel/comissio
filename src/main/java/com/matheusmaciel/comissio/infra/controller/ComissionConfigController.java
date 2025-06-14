package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.dto.comissionConfig.ComissionConfigRequestDTO;
import com.matheusmaciel.comissio.core.dto.comissionConfig.ComissionConfigResponseDTO;
import com.matheusmaciel.comissio.core.service.ComissionConfigService;
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
@RequestMapping("/comission-configs")
@Tag(   name = "Comission Configurations",
        description = "Operations for managing default comission configurations per service type"
)
public class ComissionConfigController {

    private final ComissionConfigService comissionConfigService;

    public ComissionConfigController(ComissionConfigService comissionConfigService) {
        this.comissionConfigService = comissionConfigService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new default comission configuration for a service type")
    @ApiResponse(responseCode = "201", description = "Comission configuration created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data or ServiceType not found")
    @ApiResponse(responseCode = "409", description = "Comission configuration for this service type already exists")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<ComissionConfigResponseDTO> create(@Valid @RequestBody ComissionConfigRequestDTO dto) {
        ComissionConfigResponseDTO createdConfig = this.comissionConfigService.createOrUpdateComissionConfig(dto);
        URI Location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdConfig.id())
                .toUri();
        return ResponseEntity.created(Location).body(createdConfig);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing default comission configuration")
    @ApiResponse(responseCode = "200", description = "Comission configuration updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data or ServiceType not found")
    @ApiResponse(responseCode = "404", description = "Comission configuration not found")
    @ApiResponse(responseCode = "409", description = "Conflict with another existing configuration")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<ComissionConfigResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ComissionConfigRequestDTO dto) {
        ComissionConfigResponseDTO updatedConfig = comissionConfigService.updateComissionConfig(id, dto);
        return ResponseEntity.ok(updatedConfig);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a comission configuration by its ID")
    public ResponseEntity<ComissionConfigResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(comissionConfigService.getComissionConfigById(id));
    }

    @GetMapping("/service-type/{serviceTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a comission configuration by ServiceType ID")
    public ResponseEntity<ComissionConfigResponseDTO> getByServiceTypeId(@PathVariable UUID serviceTypeId) {
        return ResponseEntity.ok(comissionConfigService.getComissionConfigByServiceTypeId(serviceTypeId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List all default comission configurations")
    public ResponseEntity<List<ComissionConfigResponseDTO>> getAll() {
        return ResponseEntity.ok(comissionConfigService.getAllComissionConfigs());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a comission configuration by ID")
    @ApiResponse(responseCode = "204", description = "Comission configuration deleted successfully")
    @ApiResponse(responseCode = "404", description = "Comission configuration not found")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        comissionConfigService.deleteComissionConfig(id);
        return ResponseEntity.noContent().build();
    }
}
