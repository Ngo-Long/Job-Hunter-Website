package vn.com.jobhunter.repository;

import org.springframework.stereotype.Repository;

import vn.com.jobhunter.domain.Company;
import vn.com.jobhunter.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);

    List<User> findByCompany(Company company);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);
}
