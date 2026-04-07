package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "policies_medical_facilities", schema = "dms_service")
public class PolicyMedicalFacility {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "policy_medical_facility_id", nullable = false)
	private UUID policyMedicalFacilityId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "policy_id", nullable = false)
	private Policy policy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medical_facility_id", nullable = false)
	private MedicalFacility medicalFacility;
}