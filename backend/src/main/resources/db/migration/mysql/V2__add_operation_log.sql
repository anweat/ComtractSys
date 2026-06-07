CREATE TABLE operation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operator_id BIGINT NOT NULL,
  operation VARCHAR(40) NOT NULL,
  detail VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_operation_log_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id),
  INDEX idx_operation_log_operator (operator_id),
  INDEX idx_operation_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
