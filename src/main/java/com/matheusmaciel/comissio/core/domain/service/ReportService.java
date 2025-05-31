package com.matheusmaciel.comissio.core.domain.service;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;

@Service
public class ReportService {

    private final PerformedServiceService performedServiceService;

    public ReportService(PerformedServiceService performedServiceService) {
        this.performedServiceService = performedServiceService;
    }

    public byte[] generateIndividualCommisionReportPdf(UUID employeeId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) throws IOException {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Relatório de Comissão Individual para Funcionário ID: " + employeeId);
                contentStream.newLine();
                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }

}

    public byte[] generateIndividualCommissionReportExcel(UUID employeeId, LocalDate startDate, LocalDate endDate) throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Comissões " + employeeId);

            // Cabeçalho da planilha
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Data Serviço");
            headerRow.createCell(1).setCellValue("Tipo Serviço");
            headerRow.createCell(2).setCellValue("Valor Serviço");
            headerRow.createCell(3).setCellValue("Comissão (%)"); // Você precisaria buscar essa info
            headerRow.createCell(4).setCellValue("Valor Comissão");
            headerRow.createCell(5).setCellValue("Status");


            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

}
