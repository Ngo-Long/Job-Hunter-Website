package vn.com.jobhunter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import vn.com.jobhunter.domain.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    List<Company> findByName(String name);
}
