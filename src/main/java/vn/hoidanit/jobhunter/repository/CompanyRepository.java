package vn.hoidanit.jobhunter.repository;

import java.util.List;
import vn.hoidanit.jobhunter.domain.Company;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    List<Company> findByName(String name);
}
