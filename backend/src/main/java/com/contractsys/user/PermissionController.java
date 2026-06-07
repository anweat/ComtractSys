package com.contractsys.user;

import com.contractsys.auth.AuthService;
import com.contractsys.auth.RequirePermission;
import com.contractsys.common.ApiException;
import com.contractsys.common.ApiResponse;
import com.contractsys.log.OperationLogService;
import com.contractsys.user.dto.PermissionRequest;
import com.contractsys.user.dto.PermissionView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequirePermission({"permission:manage", "role:manage"})
public class PermissionController {
    private final PermissionRepository permissionRepository;
    private final AuthService authService;
    private final OperationLogService operationLogService;

    public PermissionController(PermissionRepository permissionRepository, AuthService authService,
                                OperationLogService operationLogService) {
        this.permissionRepository = permissionRepository;
        this.authService = authService;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public ApiResponse<List<PermissionView>> list() {
        authService.requireUser();
        return ApiResponse.ok(permissionRepository.findAll().stream().map(PermissionView::from).toList());
    }

    @PostMapping
    @RequirePermission("permission:manage")
    public ApiResponse<PermissionView> create(@Valid @RequestBody PermissionRequest request) {
        SysUser user = authService.requireUser();
        if (permissionRepository.existsByPermissionCode(request.permissionCode())) {
            throw ApiException.conflict("权限编码已存在");
        }
        SysPermission p = new SysPermission();
        p.setPermissionCode(request.permissionCode());
        p.setPermissionName(request.permissionName());
        p.setModule(request.module());
        p.setUrl(request.url());
        p.setDescription(request.description());
        permissionRepository.save(p);
        operationLogService.log(user, "创建权限", "权限编码: " + p.getPermissionCode() + "，名称: " + p.getPermissionName());
        return ApiResponse.ok("创建成功", PermissionView.from(p));
    }

    @PutMapping("/{id}")
    @RequirePermission("permission:manage")
    public ApiResponse<PermissionView> update(@PathVariable Long id,
                                              @Valid @RequestBody PermissionRequest request) {
        SysUser user = authService.requireUser();
        SysPermission p = permissionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("权限不存在"));
        if (!p.getPermissionCode().equals(request.permissionCode())
                && permissionRepository.existsByPermissionCode(request.permissionCode())) {
            throw ApiException.conflict("权限编码已存在");
        }
        p.setPermissionCode(request.permissionCode());
        p.setPermissionName(request.permissionName());
        p.setModule(request.module());
        p.setUrl(request.url());
        p.setDescription(request.description());
        permissionRepository.save(p);
        operationLogService.log(user, "更新权限", "权限编码: " + p.getPermissionCode() + "，名称: " + p.getPermissionName());
        return ApiResponse.ok("更新成功", PermissionView.from(p));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("permission:manage")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        SysUser user = authService.requireUser();
        SysPermission p = permissionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("权限不存在"));
        permissionRepository.delete(p);
        operationLogService.log(user, "删除权限", "权限编码: " + p.getPermissionCode() + "，名称: " + p.getPermissionName());
        return ApiResponse.ok(null);
    }
}
