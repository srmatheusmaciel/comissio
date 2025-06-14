package com.matheusmaciel.comissio.core.dto.report;

public record ReportFile(
        byte[] content,
        String filename,
        String contentType
) {
}
