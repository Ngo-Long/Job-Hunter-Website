package vn.hoidanit.jobhunter.repository;

import vn.hoidanit.jobhunter.domain.Job;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    public Boolean existsByName(String name);
}
