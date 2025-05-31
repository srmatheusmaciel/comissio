package com.matheusmaciel.comissio.core.domain.model.register;

import com.matheusmaciel.comissio.infra.config.ServiceStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comission_payment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComissionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "performed_service_id", nullable = false)
    private PerformedService performedService;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid;

    @Convert(converter = ServiceStatusConverter.class)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



}
