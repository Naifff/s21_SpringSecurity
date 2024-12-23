package org.example.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
	private final AuditLogRepository auditLogRepository;

	public void logEvent(String action, String entityType, String entityId, String details) {
		AuditLog auditLog = new AuditLog(action, entityType, entityId, details);
		auditLogRepository.save(auditLog);
	}
}