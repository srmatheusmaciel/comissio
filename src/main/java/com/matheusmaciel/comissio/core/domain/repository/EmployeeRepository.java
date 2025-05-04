package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
