package vn.hoidanit.jobhunter.repository;

import java.util.List;
import vn.hoidanit.jobhunter.domain.Company;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByName(String name);
}
