CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            action VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id VARCHAR(50),
                            details TEXT,
                            created_by VARCHAR(50) NOT NULL,
                            created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);