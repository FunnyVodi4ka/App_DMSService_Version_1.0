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
@Table(name = "insured_persons_documents", schema = "dms_service")
public class InsuredPersonsDocument {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "insured_person_document_id", nullable = false)
	private UUID insuredPersonDocumentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insured_person_id", nullable = false)
	private InsuredPerson insuredPerson;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	private Document document;
}