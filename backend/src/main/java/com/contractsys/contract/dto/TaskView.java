package com.contractsys.contract.dto;

import com.contractsys.contract.ContractTask;
import com.contractsys.contract.TaskStatus;
import com.contractsys.contract.TaskType;

import java.time.LocalDateTime;

public record TaskView(
        Long id,
        Long contractId,
        String contractName,
        TaskType taskType,
        TaskStatus taskStatus,
        Long assigneeId,
        String assigneeName,
        String opinion,
        LocalDateTime operatedAt
) {
    public static TaskView from(ContractTask task) {
        return new TaskView(
                task.getId(),
                task.getContract().getId(),
                task.getContract().getName(),
                task.getTaskType(),
                task.getTaskStatus(),
                task.getAssignee().getId(),
                task.getAssignee().getDisplayName(),
                task.getOpinion(),
                task.getOperatedAt()
        );
    }

    /** 创建伪任务（待分配/待定稿） */
    public static TaskView pseudo(Long contractId, String contractName, TaskType taskType,
                                   String opinion, String assigneeName) {
        return new TaskView(-contractId, contractId, contractName, taskType,
                TaskStatus.PENDING, -1L, assigneeName, opinion, null);
    }
}

