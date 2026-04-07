package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.astondevs.mycare.models.enums.ClaimApplicationStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "claim_applications", schema = "dms_service")
@EntityListeners(AuditingEntityListener.class)
public class ClaimApplication {
    
    /**
     * Уникальный идентификатор заявки на выплату
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "claim_application_id", nullable = false)
    private UUID claimApplicationId;
    
    /**
     * Связь с полисом (policy_id)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    /**
     * Связь с застрахованным лицом (insured_person_id)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insured_person_id", nullable = false)
    private InsuredPerson insuredPerson;
    
    /**
     * ID сотрудника
     */
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    /**
     * Номер заявки (varchar 20)
     */
    @Column(name = "number", nullable = false, length = 20)
    private String number;
    
    /**
     * Сумма (decimal 10,2)
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * Номер банковского счета
     */
    @Column(name = "bank_account_number", nullable = false, length = 255)
    private String bankAccountNumber;
    
    /**
     * Статус заявки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimApplicationStatus status;
    
    /**
     * Комментарий
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    /**
     * Дата создания
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    /**
     * Дата обновления
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}