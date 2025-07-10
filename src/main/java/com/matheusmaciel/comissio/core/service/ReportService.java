package com.matheusmaciel.comissio.core.service;

import com.matheusmaciel.comissio.core.dto.report.ReportFile;
import com.matheusmaciel.comissio.core.model.register.PerformedService;
import com.matheusmaciel.comissio.core.repository.PerformedServiceRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ReportService {

    private final PerformedServiceRepository performedServiceRepository;

    public ReportService(PerformedServiceRepository performedServiceRepository) {
        this.performedServiceRepository = performedServiceRepository;
    }

    public ReportFile generateIndividualCommissionReportPdf(UUID employeeId, String employeeName, LocalDate startDate, LocalDate endDate) throws IOException {
        List<PerformedService> services = performedServiceRepository.findByEmployeeIdAndServiceDateBetween(employeeId, startDate, endDate);
        byte[] reportBytes;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontPlain = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                drawPageHeader(contentStream, employeeName, startDate, endDate, fontBold, fontPlain, dateFormatter);
                drawFooter(contentStream, fontPlain);

                float yPosition = 680;
                drawTableHeader(contentStream, yPosition, fontBold);
                yPosition -= 20;

                BigDecimal totalServicesPrice = BigDecimal.ZERO;
                BigDecimal totalCommissionAmount = BigDecimal.ZERO;

                for (PerformedService service : services) {
                    if (yPosition < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 750;
                        drawPageHeader(contentStream, employeeName, startDate, endDate, fontBold, fontPlain, dateFormatter);
                        drawTableHeader(contentStream, 680, fontBold);
                        drawFooter(contentStream, fontPlain);
                        yPosition = 660;
                    }
                    drawTableRow(contentStream, yPosition, fontPlain, service, dateFormatter, currencyFormatter);
                    yPosition -= 20;
                    totalServicesPrice = totalServicesPrice.add(service.getPrice());
                    totalCommissionAmount = totalCommissionAmount.add(service.getComissionAmount());
                }

                yPosition -= 20;
                if (yPosition < 80) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                    drawFooter(contentStream, fontPlain);
                }
                drawSummary(contentStream, yPosition, fontBold, fontPlain, totalServicesPrice, totalCommissionAmount, currencyFormatter);

            } finally {
                contentStream.close();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            reportBytes = outputStream.toByteArray();
        }

        String sanitizedEmployeeName = employeeName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String filename = "comissao_" + sanitizedEmployeeName + ".pdf";
        return new ReportFile(reportBytes, filename, "application/pdf");
    }

    public ReportFile generateIndividualCommissionReportExcel(UUID employeeId, String employeeName, LocalDate startDate, LocalDate endDate) throws IOException {
        List<PerformedService> services = performedServiceRepository.findByEmployeeIdAndServiceDateBetween(employeeId, startDate, endDate);
        byte[] reportBytes;

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Comissões - " + employeeName);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("R$ #,##0.00"));

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Data Serviço", "Serviço", "Valor Serviço (R$)", "Comissão (R$)", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;
            for (PerformedService service : services) {
                Row row = sheet.createRow(rowNum++);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(service.getServiceDate());
                dateCell.setCellStyle(dateCellStyle);
                row.createCell(1).setCellValue(service.getServiceTypeId().getName());
                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(service.getPrice().doubleValue());
                priceCell.setCellStyle(currencyCellStyle);
                Cell comissionCell = row.createCell(3);
                comissionCell.setCellValue(service.getComissionAmount().doubleValue());
                comissionCell.setCellStyle(currencyCellStyle);
                String statusText;
                switch (service.getStatus()) {
                    case COMMISSION_PAID -> statusText = "Pago";
                    case COMMISSION_PENDING -> statusText = "Pendente";
                    case CANCELLED -> statusText = "Cancelado";
                    default -> statusText = "N/A";
                }
                row.createCell(4).setCellValue(statusText);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            reportBytes = outputStream.toByteArray();
        }

        String sanitizedEmployeeName = employeeName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String filename = "comissao_" + sanitizedEmployeeName + ".xlsx";
        return new ReportFile(reportBytes, filename, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void drawPageHeader(PDPageContentStream contentStream, String employeeName, LocalDate startDate, LocalDate endDate, PDType1Font fontBold, PDType1Font fontPlain, DateTimeFormatter dateFormatter) throws IOException {
        contentStream.beginText();
        contentStream.setFont(fontBold, 16);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Relatório de Comissão Individual");
        contentStream.endText();
        contentStream.beginText();
        contentStream.setFont(fontPlain, 11);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(50, 725);
        contentStream.showText("Funcionário: " + employeeName);
        contentStream.newLine();
        contentStream.showText("Período: " + startDate.format(dateFormatter) + " a " + endDate.format(dateFormatter));
        contentStream.endText();
    }
    private void drawTableHeader(PDPageContentStream contentStream, float yPosition, PDType1Font font) throws IOException {
        contentStream.setFont(font, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Data");
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("Serviço");
        contentStream.newLineAtOffset(230, 0);
        contentStream.showText("Valor (R$)");
        contentStream.newLineAtOffset(90, 0);
        contentStream.showText("Comissão (R$)");
        contentStream.newLineAtOffset(90, 0);
        contentStream.showText("Status");
        contentStream.endText();
    }
    private void drawTableRow(PDPageContentStream contentStream, float yPosition, PDType1Font font, PerformedService service, DateTimeFormatter dateFormatter, NumberFormat currencyFormatter) throws IOException {
        String statusText;
        switch (service.getStatus()) {
            case COMMISSION_PAID -> statusText = "Pago";
            case COMMISSION_PENDING -> statusText = "Pendente";
            case CANCELLED -> statusText = "Cancelado";
            default -> statusText = "N/A";
        }
        contentStream.setFont(font, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(service.getServiceDate().format(dateFormatter));
        contentStream.newLineAtOffset(70, 0);
        String serviceName = service.getServiceTypeId().getName();
        if (serviceName.length() > 35) {
            serviceName = serviceName.substring(0, 32) + "...";
        }
        contentStream.showText(serviceName);
        contentStream.newLineAtOffset(230, 0);
        contentStream.showText(currencyFormatter.format(service.getPrice()));
        contentStream.newLineAtOffset(90, 0);
        contentStream.showText(currencyFormatter.format(service.getComissionAmount()));
        contentStream.newLineAtOffset(90, 0);
        contentStream.showText(statusText);
        contentStream.endText();
    }
    private void drawSummary(PDPageContentStream contentStream, float yPosition, PDType1Font fontBold, PDType1Font fontPlain, BigDecimal totalServices, BigDecimal totalCommission, NumberFormat currencyFormatter) throws IOException {
        contentStream.beginText();
        contentStream.setFont(fontBold, 12);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Resumo do Período:");
        contentStream.endText();
        yPosition -= 15;
        contentStream.beginText();
        contentStream.setFont(fontPlain, 11);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Valor Total dos Serviços: " + currencyFormatter.format(totalServices));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Valor Total das Comissões: " + currencyFormatter.format(totalCommission));
        contentStream.endText();
    }
    private void drawFooter(PDPageContentStream contentStream, PDType1Font font) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, 8);
        contentStream.newLineAtOffset(50, 30);
        contentStream.showText("Relatório gerado por Comissio App");
        contentStream.endText();
    }
}