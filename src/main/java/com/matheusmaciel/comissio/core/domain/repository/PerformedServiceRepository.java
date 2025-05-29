package com.matheusmaciel.comissio.core.domain.repository;

import com.matheusmaciel.comissio.core.domain.model.register.PerformedService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerformedServiceRepository extends JpaRepository<PerformedService, UUID> {
}
