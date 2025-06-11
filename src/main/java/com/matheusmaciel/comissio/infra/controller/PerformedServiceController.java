package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceUpdateRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import com.matheusmaciel.comissio.core.domain.service.PerformedServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/performed-services")
@Tag(name = "Performed Services", description = "Operations for registering services performed by employees and their comissions")
@SecurityRequirement(name = "jwt_auth")
public class PerformedServiceController {
    private final PerformedServiceService performedServiceService;

    public PerformedServiceController(PerformedServiceService performedServiceService) {
        this.performedServiceService = performedServiceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Register a new service performed and calculate your commission")
    @ApiResponse(responseCode = "201", description = "Service performed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Employee or ServiceType not found")
    @ApiResponse(responseCode = "409", description = "Employee already performed this service")
    public ResponseEntity<PerformedServiceResponseDTO> registerService(
            @Valid @RequestBody PerformedServiceRequestDTO dto) {

        PerformedServiceResponseDTO response = performedServiceService.createPerformedService(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);

    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel a performed service")
    @ApiResponse(responseCode = "200", description = "Service canceled successfully")
    @ApiResponse(responseCode = "404", description = "Service not found")
    public ResponseEntity<PerformedServiceResponseDTO> cancelService(@PathVariable UUID id) {
        PerformedServiceResponseDTO responseDto = performedServiceService.cancelPerformedService(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a performed service")
    @ApiResponse(responseCode = "200", description = "Service updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Service not found")
    public ResponseEntity<PerformedServiceResponseDTO> updateService(@PathVariable UUID id,
                                                                     @Valid @RequestBody PerformedServiceUpdateRequestDTO dto) {
        PerformedServiceResponseDTO responseDto = performedServiceService.updatePerformedService(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a performed service")
    @ApiResponse(responseCode = "200", description = "Service deleted successfully")
    @ApiResponse(responseCode = "404", description = "Service not found")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        performedServiceService.deletePerformedService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all performed services with pagination",
            description = "Returns a paginated list of all registered services." +
                    "You can control pagination and sorting using the parameters:" +
                    " page, size, and sort (e.g. sort=serviceDate,desc).")
    @PageableAsQueryParam
    @ApiResponse(responseCode = "200", description = "List of performed services retrieved")
    public ResponseEntity<Page<PerformedServiceResponseDTO>> getAllPerformedServices(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) ServiceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable, Authentication authentication) {

        Page<PerformedServiceResponseDTO> responsePage = performedServiceService.getAllPerformedServices(
                employeeId, status, startDate, endDate, pageable, authentication);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a performed service by id")
    @ApiResponse(responseCode = "200", description = "Performed service found")
    @ApiResponse(responseCode = "404", description = "Performed service not found")
    public ResponseEntity<PerformedServiceResponseDTO> getPerformedServiceById(@PathVariable UUID id) {
        PerformedServiceResponseDTO response = performedServiceService.getPerformedServiceById(id);
        return ResponseEntity.ok(response);
    }


    //payment
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark the commission for a completed service as paid",
              description = "Updates the service status to COMMISSION_PAID and records the commission payment.")
    @ApiResponse(responseCode = "200", description = "Commission marked as paid and payment registered successfully.")
    @ApiResponse(responseCode = "400", description = "Unable to mark as paid (e.g. invalid status).")
    @ApiResponse(responseCode = "404", description = "Service performed not found.")
    public ResponseEntity<PerformedServiceResponseDTO> markServiceAsPaid(@PathVariable UUID id) {
        PerformedServiceResponseDTO response = performedServiceService.markCommissionAsPaid(id);
        return ResponseEntity.ok(response);
    }

}
