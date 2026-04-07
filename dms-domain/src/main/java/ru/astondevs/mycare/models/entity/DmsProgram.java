package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.astondevs.mycare.models.enums.DmsType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dms_programs", schema = "dms_service")
public class DmsProgram {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "dms_program_id", nullable = false)
	private UUID dmsProgramId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "dms_type", nullable = false)
	private DmsType dmsType;
	
	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;
	
	@Column(name = "clinic_service", nullable = false)
	private boolean clinicService;
	
	@Column(name = "hospitalization", nullable = false)
	private boolean hospitalization;
	
	@Column(name = "stomatology", nullable = false)
	private boolean stomatology;
	
	@Column(name = "ambulance", nullable = false)
	private boolean ambulance;
	
	@Column(name = "calling_doctor", nullable = false)
	private boolean callingDoctor;
	
	@Column(name = "telemedicine", nullable = false)
	private boolean telemedicine;
}