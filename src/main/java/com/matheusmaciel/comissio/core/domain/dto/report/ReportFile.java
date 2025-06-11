package com.matheusmaciel.comissio.core.domain.dto.report;

public record ReportFile(
        byte[] content,
        String filename,
        String contentType
) {
}
