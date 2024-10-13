package vn.hoidanit.jobhunter.controller;

import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role dataRole) throws IdInvalidException {
        // check name
        if (this.roleService.existByName(dataRole.getName())) {
            throw new IdInvalidException("Role với name = " + dataRole.getName() + " đã tồn tại");
        }

        // create a role
        Role newRole = this.roleService.handleCreateRole(dataRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role dataRole) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(dataRole.getId()) == null) {
            throw new IdInvalidException("Role với id = " + dataRole.getId() + " không tồn tại");
        }

        // check name
        if (this.roleService.existByName(dataRole.getName())) {
            throw new IdInvalidException("Role với name = " + dataRole.getName() + " đã tồn tại");
        }

        // update a role
        Role currentRole = this.roleService.handleUpdateRole(dataRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(currentRole);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role with id = " + id + " not found!");
        }

        // delete a role
        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable) {
        ResultPaginationDTO dataRoles = this.roleService.getRoles(spec, pageable);
        return ResponseEntity.ok(dataRoles);
    }

}
