package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PerformedServiceRepository extends JpaRepository<PerformedService, UUID>, JpaSpecificationExecutor<PerformedService> {
    List<PerformedService> findByEmployee_IdAndStatusAndServiceDateLessThanEqual(
            UUID employeeId,
            ServiceStatus status,
            LocalDate serviceDate
    );

    List<PerformedService> findByEmployee_IdAndStatus(UUID employeeId, ServiceStatus status);

    List<PerformedService> findByEmployeeIdAndServiceDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

}
