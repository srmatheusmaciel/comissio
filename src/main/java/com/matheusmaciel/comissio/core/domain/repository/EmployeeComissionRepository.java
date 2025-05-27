package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.EmployeeComission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeComissionRepository extends JpaRepository<EmployeeComission, UUID> {
    Optional<EmployeeComission> findByEmployeeIdAndServiceTypeId(UUID employeeId, UUID serviceTypeId);

    Optional<EmployeeComission> findByEmployeeId(UUID employeeId);

    Optional<EmployeeComission> findByServiceTypeId(UUID serviceTypeId);
}
