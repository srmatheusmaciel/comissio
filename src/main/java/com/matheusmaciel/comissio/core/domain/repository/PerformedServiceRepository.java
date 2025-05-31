package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PerformedServiceRepository extends JpaRepository<PerformedService, UUID> {
    List<PerformedService> findByEmployee_IdAndStatusAndServiceDateLessThanEqual(
            UUID employeeId,
            ServiceStatus status,
            LocalDate serviceDate
    );

    List<PerformedService> findByEmployee_IdAndStatus(UUID employeeId, ServiceStatus status);

}
