package com.contractsys.log;

import com.contractsys.common.PageRequests;
import com.contractsys.user.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {
    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    public void log(SysUser operator, String operation, String detail) {
        operationLogRepository.save(new OperationLog(operator, operation, detail));
    }

    public Page<OperationLog> list(String keyword, int page, int size) {
        var pr = PageRequests.of(page, size);
        if (keyword == null || keyword.isEmpty()) {
            return operationLogRepository.findAllOrderByCreatedAtDesc(pr);
        }
        return operationLogRepository.searchByKeyword(keyword, pr);
    }
}
