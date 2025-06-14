package com.matheusmaciel.comissio.core.model.register;

import com.matheusmaciel.comissio.infra.config.ServiceStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performed_service")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PerformedService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceTypeId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name="comission_amount", nullable = false)
    private BigDecimal comissionAmount;

    @Convert(converter = ServiceStatusConverter.class)
    @Column(name = "status", nullable = false)
    private ServiceStatus status;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
