package vn.hoidanit.jobhunter.controller;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission dataPermission)
            throws IdInvalidException {
        if (this.permissionService.isPermissionExist(dataPermission)) {
            throw new IdInvalidException("Permission already exists!");
        }

        // create new permission
        Permission newPermission = this.permissionService.handleCreatePermission(dataPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission dataPermission)
            throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(dataPermission.getId()) == null) {
            throw new IdInvalidException("Permission with id = " + dataPermission.getId() + " not found!");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(dataPermission)) {
            throw new IdInvalidException("Permission already exists!");
        }

        // update permission
        Permission currentPermission = this.permissionService.handleUpdatePermission(dataPermission);
        return ResponseEntity.ok().body(currentPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission with id = " + id + " not found!");
        }

        // delete a permission
        this.permissionService.deletePermissionById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> spec, Pageable pageable) {
        ResultPaginationDTO dataPermissions = this.permissionService.getPermissions(spec, pageable);
        return ResponseEntity.ok(dataPermissions);
    }
}
