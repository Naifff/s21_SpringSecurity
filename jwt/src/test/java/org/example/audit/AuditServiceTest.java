package org.example.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuditServiceTest {

	@Autowired
	private AuditService auditService;

	@Autowired
	private AuditLogRepository auditLogRepository;

	@Test
	@WithMockUser(username = "testuser")
	@Transactional
	void whenLogEvent_thenAuditLogIsCreated() {
		auditService.logEvent("LOGIN", "USER", "1", "User login successful");

		List<AuditLog> logs = auditLogRepository.findAll();
		assertThat(logs).hasSize(1);
		AuditLog log = logs.get(0);
		assertThat(log.getAction()).isEqualTo("LOGIN");
		assertThat(log.getEntityType()).isEqualTo("USER");
		assertThat(log.getEntityId()).isEqualTo("1");
		assertThat(log.getDetails()).isEqualTo("User login successful");
		assertThat(log.getCreatedBy()).isEqualTo("testuser");
	}

	@Test
	@Transactional
	void whenLogEventWithoutUser_thenSystemUserIsRecorded() {
		auditService.logEvent("SYSTEM_EVENT", "SYSTEM", "0", "System startup");

		List<AuditLog> logs = auditLogRepository.findAll();
		assertThat(logs).hasSize(1);
		AuditLog log = logs.get(0);
		assertThat(log.getCreatedBy()).isEqualTo("system");
	}
}