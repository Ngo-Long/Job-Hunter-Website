package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role dataRole) {
        // check permissions
        if (dataRole.getPermissions() != null) {
            List<Long> reqPermissions = dataRole.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            dataRole.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(dataRole);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }

        return null;
    }

    public Role handleUpdateRole(Role dataRole) {
        Role roleDB = this.fetchRoleById(dataRole.getId());

        // check permissions
        if (dataRole.getPermissions() != null) {
            List<Long> reqPermissions = dataRole.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            dataRole.setPermissions(dbPermissions);
        }

        roleDB.setName(dataRole.getName());
        roleDB.setDescription(dataRole.getDescription());
        roleDB.setActive(dataRole.isActive());
        roleDB.setPermissions(dataRole.getPermissions());
        this.roleRepository.save(roleDB);

        return roleDB;
    }

    public void deleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pRole.getTotalPages());
        mt.setTotal(pRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pRole.getContent());

        return rs;
    }
}
