package com.matheusmaciel.comissio.infra.controller;


import com.matheusmaciel.comissio.core.dto.report.ReportFile;
import com.matheusmaciel.comissio.core.service.EmailService;
import com.matheusmaciel.comissio.core.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Endpoints for generating and downloading reports")
@SecurityRequirement(name = "jwt_auth")
public class ReportController {

    private final ReportService reportService;
    private final EmailService emailService;

    public ReportController(ReportService reportService, EmailService emailService) {
        this.emailService = emailService;
        this.reportService = reportService;
    }

    @GetMapping("/employees/{employeeId}/commissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Gerar relatório de comissão individual para um funcionário")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<byte[]> getIndividualCommissionReport(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format) throws IOException {

        ReportFile reportFile;

        if ("excel".equalsIgnoreCase(format)) {
            reportFile = reportService.generateIndividualCommissionReportExcel(employeeId, startDate, endDate);
        } else {
            reportFile = reportService.generateIndividualCommisionReportPdf(employeeId, startDate, endDate);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportFile.contentType()));
        headers.setContentDispositionFormData("attachment", reportFile.filename());

        return new ResponseEntity<>(reportFile.content(), headers, HttpStatus.OK);
    }

    @GetMapping("/my-commissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Gerar relatório de comissão para o funcionário autenticado")
    @ApiResponse(responseCode = "200", description = "Relatório pessoal gerado com sucesso")
    public ResponseEntity<byte[]> getMyCommissionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format, Authentication authentication) throws IOException {

        ReportFile reportFile = reportService.generateMyCommissionReport(authentication, startDate, endDate, format);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportFile.contentType()));
        headers.setContentDispositionFormData("attachment", reportFile.filename());

        return new ResponseEntity<>(reportFile.content(), headers, HttpStatus.OK);
    }

    @PostMapping("/employees/{employeeId}/commissions/send-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Enviar relatório de comissão individual por e-mail")
    @ApiResponse(responseCode = "200", description = "E-mail com relatório enviado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro ao gerar relatório ou enviar e-mail")
    public ResponseEntity<String> sendIndividualCommissionReportByEmail(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam String toEmail
    ) {
        try {

            emailService.sendCommissionReportByEmail(toEmail, employeeId, startDate, endDate, format);
            return ResponseEntity.ok("Relatório enviado para " + toEmail);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar o relatório: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

}