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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.astondevs.mycare.models.enums.DocumentStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents", schema = "dms_service")
@EntityListeners(AuditingEntityListener.class)
public class Document {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "document_id", nullable = false)
	private UUID documentId;
	
	@Column(name = "type", nullable = false, length = 150)
	private String type;
	
	@Column(name = "document_url", nullable = false, length = 255)
	private String documentUrl;
	
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DocumentStatus status;
	
	@Column(name = "comment", columnDefinition = "TEXT")
	private String comment;
}