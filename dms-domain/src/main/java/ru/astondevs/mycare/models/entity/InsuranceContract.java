package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.astondevs.mycare.models.enums.InsuranceContractStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "insurance_contracts", schema = "dms_service")
public class InsuranceContract {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "insurance_contract_id", nullable = false)
	private UUID insuranceContractId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insurance_application_id", nullable = false)
	private InsuranceApplication insuranceApplication;
	
	@Column(name = "number", nullable = false, length = 50)
	private String number;
	
	@Column(name = "insurance_contract_url", nullable = false, length = 255)
	private String insuranceContractUrl;
	
	@Column(name = "bank_account_number", nullable = false, length = 255)
	private String bankAccountNumber;
	
	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;
	
	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;
	
	@Column(name = "issued_at", nullable = false)
	private LocalDate issuedAt;
	
	@Column(name = "paid_at", nullable = false)
	private LocalDate paidAt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private InsuranceContractStatus status;
}