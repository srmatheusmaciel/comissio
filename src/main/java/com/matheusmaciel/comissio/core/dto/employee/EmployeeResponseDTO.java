package com.matheusmaciel.comissio.core.dto.employee;

import com.matheusmaciel.comissio.core.model.access.User;
import com.matheusmaciel.comissio.core.model.register.Employee;
import com.matheusmaciel.comissio.core.model.register.StatusEmployee;

import java.time.LocalDateTime;
import java.util.UUID;


public record EmployeeResponseDTO(
        UUID id,
        UUID userId,
        String name,
        String email,
        StatusEmployee status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
        public static EmployeeResponseDTO fromEntity(Employee employee) {
                if(employee == null) return null;

                User user = employee.getUser();

                if(user == null) {
                        return new EmployeeResponseDTO(
                                employee.getId(),
                                null,
                                null,
                                null,
                                employee.getStatus(),
                                employee.getCreated_at(),
                                employee.getUpdated_at()
                        );
                }

                return new EmployeeResponseDTO(
                        employee.getId(),
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        employee.getStatus(),
                        employee.getCreated_at(),
                        employee.getUpdated_at()
                );

        }
}