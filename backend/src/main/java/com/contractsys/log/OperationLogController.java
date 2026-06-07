package com.contractsys.log;

import com.contractsys.auth.AuthService;
import com.contractsys.auth.RequirePermission;
import com.contractsys.common.ApiResponse;
import com.contractsys.common.PageResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operations")
@RequirePermission("log:view")
public class OperationLogController {
    private final OperationLogService operationLogService;
    private final AuthService authService;

    public OperationLogController(OperationLogService operationLogService, AuthService authService) {
        this.operationLogService = operationLogService;
        this.authService = authService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OperationLogView>> list(@RequestParam(defaultValue = "") String keyword,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        authService.requireUser();
        var result = operationLogService.list(keyword, page, size);
        return ApiResponse.ok(PageResponse.from(result.map(OperationLogView::from)));
    }
}
