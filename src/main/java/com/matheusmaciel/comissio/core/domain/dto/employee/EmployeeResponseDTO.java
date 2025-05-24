package com.matheusmaciel.comissio.core.domain.dto.employee;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.StatusEmployee;

import java.time.LocalDateTime;
import java.util.UUID;


public record EmployeeResponseDTO(
        UUID id,
        UUID userId,
        StatusEmployee status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
        public static EmployeeResponseDTO fromEntity(Employee employee) {
                if(employee == null) return null;

                User user = employee.getUser();

                String name = null;
                String email = null;
                if(user != null) {
                        name = user.getName();
                        email = user.getEmail();
                }

                return new EmployeeResponseDTO(employee.getId(),
                user.getId() != null ? employee.getUser().getId() : null,
                employee.getStatus(),
                employee.getCreated_at(),
                employee.getUpdated_at()
                );

        }
}