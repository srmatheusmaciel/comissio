package com.matheusmaciel.comissio.core.domain.model.register;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comission_configuration")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComissionConfig {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @Column(name = "default_percentage", nullable = false)
    private BigDecimal defaultPercentage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
