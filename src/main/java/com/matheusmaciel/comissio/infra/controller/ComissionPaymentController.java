package com.matheusmaciel.comissio.infra.controller;


import com.matheusmaciel.comissio.core.domain.dto.comissionPayment.BatchPaymentRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.comissionPayment.BatchPaymentResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.comissionPayment.ComissionPaymentResponseDTO;
import com.matheusmaciel.comissio.core.domain.service.ComissionPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comission-payments")
@Tag(name = "Comission Payments", description = "Operations related to comission payments")
@SecurityRequirement(name = "jwt_auth")
public class ComissionPaymentController {

    private final ComissionPaymentService comissionPaymentService;

    public ComissionPaymentController(ComissionPaymentService comissionPaymentService) {
        this.comissionPaymentService = comissionPaymentService;
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // Conforme RN005
    @Operation(summary = "Processar pagamento de comissões em lote para um funcionário",
            description = "Paga todas as comissões pendentes de um funcionário (opcionalmente até uma data de serviço específica), " +
                    "atualizando o status dos serviços e criando registros de pagamento individuais para cada comissão.")
    @ApiResponse(responseCode = "200", description = "Pagamento em lote processado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou nenhuma comissão pendente encontrada.")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado.")
    public ResponseEntity<BatchPaymentResponseDTO> processBatchPayment(
            @Valid @RequestBody BatchPaymentRequestDTO dto) {
        BatchPaymentResponseDTO response = comissionPaymentService.processBatchPayment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar todos os registros de pagamento de comissão com paginação",
            description = "Retorna uma lista paginada de todos os pagamentos de comissão registrados.")
    @PageableAsQueryParam // Documenta os parâmetros de paginação para o Swagger
    @ApiResponse(responseCode = "200", description = "Lista paginada de pagamentos obtida com sucesso.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<ComissionPaymentResponseDTO>> getAllComissionPayments(Pageable pageable) {
        Page<ComissionPaymentResponseDTO> responsePage = comissionPaymentService.getAllComissionPayments(pageable);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar um registro de pagamento de comissão por ID")
    @ApiResponse(responseCode = "200", description = "Pagamento de comissão encontrado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissionPaymentResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pagamento de comissão não encontrado.")
    public ResponseEntity<ComissionPaymentResponseDTO> getComissionPaymentById(@PathVariable UUID id) {
        ComissionPaymentResponseDTO responseDto = comissionPaymentService.getComissionPaymentById(id);
        return ResponseEntity.ok(responseDto);
    }



}