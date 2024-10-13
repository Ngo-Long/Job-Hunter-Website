package vn.hoidanit.jobhunter.repository;

import java.util.List;

import vn.hoidanit.jobhunter.domain.Permission;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>,
                JpaSpecificationExecutor<Permission> {
        boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

        List<Permission> findByIdIn(List<Long> id);
}
