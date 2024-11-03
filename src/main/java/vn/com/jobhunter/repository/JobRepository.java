package vn.com.jobhunter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import vn.com.jobhunter.domain.Job;
import vn.com.jobhunter.domain.Skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    public Boolean existsByName(String name);

    List<Job> findBySkillsIn(List<Skill> skills);
}
