package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    public boolean existsByUser_Id(UUID id);

    Optional<Employee> findByUser_Id(UUID id);

}
