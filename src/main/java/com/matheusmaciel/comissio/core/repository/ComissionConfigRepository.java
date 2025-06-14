package com.matheusmaciel.comissio.core.repository;

import com.matheusmaciel.comissio.core.model.register.ComissionConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComissionConfigRepository extends JpaRepository<ComissionConfig, UUID> {

    Optional<ComissionConfig> findByServiceTypeId(UUID serviceTypeId);
}
