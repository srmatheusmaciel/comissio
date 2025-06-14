package com.matheusmaciel.comissio.core.repository;

import com.matheusmaciel.comissio.core.model.register.ComissionPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComissionPaymentRepository extends JpaRepository<ComissionPayment, UUID> {
}
