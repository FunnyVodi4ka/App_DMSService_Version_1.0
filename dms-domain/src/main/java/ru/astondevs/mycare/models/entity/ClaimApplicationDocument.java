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
@Table(name = "claim_applications_documents", schema = "dms_service")
public class ClaimApplicationDocument {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "claim_application_document_id", nullable = false)
	private UUID claimApplicationDocumentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_application_id", nullable = false)
	private ClaimApplication claimApplication;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	private Document document;
}