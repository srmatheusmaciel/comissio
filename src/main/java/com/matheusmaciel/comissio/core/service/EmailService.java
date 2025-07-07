package com.matheusmaciel.comissio.core.service;

import com.matheusmaciel.comissio.core.dto.report.ReportFile;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
        ReportFile reportFile;
        String subject = "Seu Relatório de Comissões - Período: " + startDate + " a " + endDate;
        String body = "Olá, segue em anexo o seu relatório de comissões.";


        if ("excel".equalsIgnoreCase(format)) {
            reportFile = reportService.generateIndividualCommissionReportExcel(employeeId, startDate, endDate);

        } else {
            reportFile = reportService.generateIndividualCommisionReportPdf(employeeId, startDate, endDate);

        }

        sendEmailWithAttachment(toEmail, subject, body, reportFile.content(), reportFile.filename(), reportFile.contentType());
    }

    public void sendPasswordResetEmail(String to, String name, String resetLink){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Comissio - Recuperação de Senha");
            
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>Recuperação de Senha</h2>
                    <p>Olá, %s!</p>
                    <p>Recebemos uma solicitação para redefinir a senha da sua conta no sistema Comissio. Se não foi você, por favor, ignore este e-mail.</p>
                    <p>Para criar uma nova senha, clique no link abaixo. Ele é válido por 1 hora.</p>
                    <p style="margin: 25px 0;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Redefinir Senha
                        </a>
                    </p>
                    <p>Atenciosamente,<br>Equipe Comissio</p>
                </div>
            """, name, resetLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);



        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de recuperação de senha.", e);
        }
    }


}