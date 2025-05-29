package com.matheusmaciel.comissio.infra.config;

import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import jakarta.persistence.AttributeConverter;

public class ServiceStatusConverter implements AttributeConverter<ServiceStatus, String> {
    @Override
    public String convertToDatabaseColumn(ServiceStatus status) {
        return status == null ? null : status.name();
    }

    @Override
    public ServiceStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ServiceStatus.valueOf(dbData);
    }
}
