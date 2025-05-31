package com.matheusmaciel.comissio.core.domain.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final ReportService reportService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, ReportService reportService) {
        this.mailSender = mailSender;
        this.reportService = reportService;
    }

    public void sendEmailWithAttachment(String to, String subject, String body,
                                        byte[] attachmentBytes, String attachmentFilename, String attachmentContentType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indica multipart

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            helper.addAttachment(attachmentFilename, new ByteArrayResource(attachmentBytes), attachmentContentType);

            mailSender.send(message);
            System.out.println("Email enviado para: " + to + " com anexo: " + attachmentFilename); // Log
        } catch (MessagingException e) {

            throw new RuntimeException("Erro ao enviar email com anexo: " + e.getMessage(), e);
        }
    }


    public void sendCommissionReportByEmail(String toEmail, UUID employeeId,
                                            LocalDate startDate, LocalDate endDate, String format) throws IOException {
        byte[] reportBytes;
        String filename;
        String contentType;
        String subject = "Seu Relatório de Comissões - Período: " + startDate + " a " + endDate;
        String body = "Olá, segue em anexo o seu relatório de comissões.";


        if ("excel".equalsIgnoreCase(format)) {
            reportBytes = reportService.generateIndividualCommissionReportExcel(employeeId, startDate, endDate);
            filename = "comissoes_" + employeeId + "_" + startDate + "_a_" + endDate + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            reportBytes = reportService.generateIndividualCommisionReportPdf(employeeId, startDate, endDate);
            filename = "comissoes_" + employeeId + "_" + startDate + "_a_" + endDate + ".pdf";
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        sendEmailWithAttachment(toEmail, subject, body, reportBytes, filename, contentType);
    }
}