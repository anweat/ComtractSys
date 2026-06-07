package com.contractsys.log;

import java.time.LocalDateTime;

public record OperationLogView(
        Long id,
        String operatorName,
        String operation,
        String detail,
        LocalDateTime createdAt
) {
    public static OperationLogView from(OperationLog log) {
        return new OperationLogView(
                log.getId(),
                log.getOperator().getDisplayName(),
                log.getOperation(),
                log.getDetail(),
                log.getCreatedAt()
        );
    }
}
