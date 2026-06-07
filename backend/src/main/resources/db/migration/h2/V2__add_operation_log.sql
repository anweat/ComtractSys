CREATE TABLE operation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operator_id BIGINT NOT NULL,
  operation VARCHAR(40) NOT NULL,
  detail VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fk_operation_log_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id)
);

CREATE INDEX idx_operation_log_operator ON operation_log (operator_id);
CREATE INDEX idx_operation_log_created_at ON operation_log (created_at);
