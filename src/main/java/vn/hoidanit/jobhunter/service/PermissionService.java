package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }

        return null;
    }

    public Permission handleCreatePermission(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission handleUpdatePermission(Permission p) {
        Permission permissionDB = this.fetchPermissionById(p.getId());
        if (permissionDB == null) {
            return null;
        }

        // set
        permissionDB.setName(p.getName());
        permissionDB.setApiPath(p.getApiPath());
        permissionDB.setMethod(p.getMethod());
        permissionDB.setModule(p.getModule());

        // update
        permissionDB = this.permissionRepository.save(permissionDB);

        return permissionDB;
    }

    public void deletePermissionById(long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);

        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pPermissions.getTotalPages());
        mt.setTotal(pPermissions.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pPermissions.getContent());

        return rs;
    }

    public boolean isSameName(Permission dataPermission) {
        Permission permissionDB = this.fetchPermissionById(dataPermission.getId());
        if (permissionDB == null) {
            return false;
        }

        if (!permissionDB.getName().equals(dataPermission.getName())) {
            return false;
        }

        return true;
    }
}
