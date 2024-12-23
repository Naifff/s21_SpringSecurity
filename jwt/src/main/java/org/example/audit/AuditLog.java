package org.example.audit;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String action;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "entity_id")
	private String entityId;

	@Column(columnDefinition = "TEXT")
	private String details;

	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;

	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public AuditLog(String action, String entityType, String entityId, String details) {
		this.action = action;
		this.entityType = entityType;
		this.entityId = entityId;
		this.details = details;
	}
}



