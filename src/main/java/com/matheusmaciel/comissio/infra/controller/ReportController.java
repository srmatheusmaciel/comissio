package com.matheusmaciel.comissio.infra.controller;


import com.matheusmaciel.comissio.core.dto.employee.EmployeeResponseDTO;
import com.matheusmaciel.comissio.core.dto.report.ReportFile;
import com.matheusmaciel.comissio.core.model.access.User;
import com.matheusmaciel.comissio.core.service.EmailService;
import com.matheusmaciel.comissio.core.service.EmployeeService;
import com.matheusmaciel.comissio.core.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class ReportController {

    private final ReportService reportService;
    private final EmailService emailService;
    private final EmployeeService employeeService;

    public ReportController(ReportService reportService, EmailService emailService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.emailService = emailService;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees/{employeeId}/commissions")
    public ResponseEntity<byte[]> getIndividualCommissionReport(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format) throws IOException {

        // Controller busca os dados do funcionário
        EmployeeResponseDTO employee = employeeService.getEmployeeById(employeeId);

        ReportFile reportFile;
        if ("excel".equalsIgnoreCase(format)) {
            // Passa o nome para o serviço
            reportFile = reportService.generateIndividualCommissionReportExcel(employeeId, employee.name(), startDate, endDate);
        } else {
            reportFile = reportService.generateIndividualCommissionReportPdf(employeeId, employee.name(), startDate, endDate);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportFile.contentType()));
        headers.setContentDispositionFormData("attachment", reportFile.filename());

        return new ResponseEntity<>(reportFile.content(), headers, HttpStatus.OK);
    }

    @GetMapping("/my-commissions")
    public ResponseEntity<byte[]> getMyCommissionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format,
            Authentication authentication) throws IOException {

        User authenticatedUser = (User) authentication.getPrincipal();
        EmployeeResponseDTO employee = employeeService.findByUserIdAndReturnDto(authenticatedUser.getId());

        ReportFile reportFile;
        if ("excel".equalsIgnoreCase(format)) {
            reportFile = reportService.generateIndividualCommissionReportExcel(employee.id(), employee.name(), startDate, endDate);
        } else {
            reportFile = reportService.generateIndividualCommissionReportPdf(employee.id(), employee.name(), startDate, endDate);
        }

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