package vn.com.jobhunter.repository;

import org.springframework.stereotype.Repository;

import vn.com.jobhunter.domain.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
                JpaSpecificationExecutor<Role> {
        boolean existsByName(String name);

        Role findByName(String name);
}
