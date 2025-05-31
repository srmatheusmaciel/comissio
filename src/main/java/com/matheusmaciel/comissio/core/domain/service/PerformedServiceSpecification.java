package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceStatus; // Ajuste o import
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.UUID;

public class PerformedServiceSpecification {
    public static Specification<PerformedService> employeeIdEquals(UUID employeeId) {
        if (employeeId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Join<PerformedService, Employee> employeeJoin = root.join("employee");
            return criteriaBuilder.equal(employeeJoin.get("id"), employeeId);
        };
    }

    public static Specification<PerformedService> statusEquals(ServiceStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<PerformedService> serviceDateGreaterThanOrEquals(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("serviceDate"), startDate);
    }

    public static Specification<PerformedService> serviceDateLessThanOrEquals(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("serviceDate"), endDate);
    }
}
