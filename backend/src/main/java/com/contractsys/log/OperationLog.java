package com.contractsys.log;

import com.contractsys.user.SysUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private SysUser operator;

    @Column(nullable = false, length = 40)
    private String operation;

    @Column(length = 500)
    private String detail;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OperationLog() {}

    public OperationLog(SysUser operator, String operation, String detail) {
        this.operator = operator;
        this.operation = operation;
        this.detail = detail;
    }

    public Long getId() { return id; }
    public SysUser getOperator() { return operator; }
    public String getOperation() { return operation; }
    public String getDetail() { return detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
