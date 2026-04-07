package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.astondevs.mycare.models.enums.MedicalFacilitiesType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_facilities", schema = "dms_service")
@EntityListeners(AuditingEntityListener.class)
public class MedicalFacility {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "medical_facility_id", nullable = false)
	private UUID medicalFacilityId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private MedicalFacilitiesType type;
	
	@Column(name = "name", nullable = false, length = 150)
	private String name;
	
	@Column(name = "address", nullable = false, length = 255)
	private String address;
	
	@Column(name = "phone_number", nullable = false, length = 20)
	private String phoneNumber;
	
	@Column(name = "email", nullable = false, length = 50)
	private String email;
	
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}