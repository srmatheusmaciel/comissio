package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.employee.EmployeeResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.repository.PerformedServiceRepository;
import com.matheusmaciel.comissio.infra.exception.employee.EmployeeNotFoundException;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;

@Service
public class ReportService {

    private final PerformedServiceService performedServiceService;
    private final PerformedServiceRepository performedServiceRepository;
    private final EmployeeService employeeService;

    public ReportService(PerformedServiceService performedServiceService,
                         PerformedServiceRepository performedServiceRepository,
                         EmployeeService employeeService) {
        this.performedServiceService = performedServiceService;
        this.performedServiceRepository = performedServiceRepository;
        this.employeeService = employeeService;
    }

    public byte[] generateIndividualCommisionReportPdf(UUID employeeId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) throws IOException {

        List<PerformedService> services = performedServiceRepository.findByEmployeeIdAndServiceDateBetween(employeeId, startDate, endDate);
        EmployeeResponseDTO employee = employeeService.getEmployeeById(employeeId);

        BigDecimal totalServicesPrice = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;


        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontPlain = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                Locale brLocale = new Locale("pt", "BR");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(brLocale);

                drawPageHeader(contentStream, employee.name(), startDate, endDate, fontBold, fontPlain, dateFormatter);
                drawFooter(contentStream, fontPlain);

                float yPosition = 680;
                drawTableHeader(contentStream, yPosition, fontBold);

                yPosition -= 20;

                for (PerformedService service : services) {

                    if (yPosition < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 750;

                        drawPageHeader(contentStream, employee.name(), startDate, endDate, fontBold, fontPlain, dateFormatter);
                        drawTableHeader(contentStream, yPosition, fontBold);
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
            return outputStream.toByteArray();
        }
    }

    private void drawPageHeader(PDPageContentStream contentStream, String employeeName, LocalDate startDate, LocalDate endDate,
                                PDType1Font fontBold, PDType1Font fontPlain, DateTimeFormatter dateFormatter) throws IOException {
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

    private void drawTableRow(PDPageContentStream contentStream, float yPosition, PDType1Font font, PerformedService service,
                              DateTimeFormatter dateFormatter, NumberFormat currencyFormatter) throws IOException {

        Locale brLocale = new Locale("pt", "BR");
        DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormatter2 = NumberFormat.getCurrencyInstance(brLocale);

        String statusText;

        switch (service.getStatus()) {
            case COMMISSION_PAID:
                statusText = "Paid";
                break;
            case COMMISSION_PENDING:
                statusText = "Pending";
                break;
            case CANCELLED:
                statusText = "Cancelled";
                break;
            default:
                statusText = "N/A";
                break;
        }


        contentStream.setFont(font, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(service.getServiceDate().format(dateFormatter2));

        contentStream.newLineAtOffset(70, 0);
        String serviceName = service.getServiceTypeId().getName();
        if (serviceName.length() > 35) {
            serviceName = serviceName.substring(0, 27) + "...";
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

    private void drawSummary(PDPageContentStream contentStream, float yPosition, PDType1Font fontBold, PDType1Font fontPlain,
                             BigDecimal totalServices, BigDecimal totalCommission, NumberFormat currencyFormatter) throws IOException {
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

    // Dentro da classe ReportService.java

    private void drawFooter(PDPageContentStream contentStream, PDType1Font font) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, 8);
        contentStream.newLineAtOffset(50, 30);
        contentStream.showText("Relatório gerado por COMISSIO APP");
        contentStream.endText();
    }




public byte[] generateIndividualCommissionReportExcel(UUID employeeId, LocalDate startDate, LocalDate endDate) throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Comissões " + employeeId);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Data Serviço");
            headerRow.createCell(1).setCellValue("Tipo Serviço");
            headerRow.createCell(2).setCellValue("Valor Serviço");
            headerRow.createCell(3).setCellValue("Comissão (%)");
            headerRow.createCell(4).setCellValue("Valor Comissão");
            headerRow.createCell(5).setCellValue("Status");


            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

}
