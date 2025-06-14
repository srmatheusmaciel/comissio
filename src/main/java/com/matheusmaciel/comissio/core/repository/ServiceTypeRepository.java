package com.matheusmaciel.comissio.core.repository;

import com.matheusmaciel.comissio.core.model.register.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, UUID> {
    @Override
    Optional<ServiceType> findById(UUID id);

    Optional<ServiceType> findByName(String name);
}
