package vn.hoidanit.jobhunter.repository;

import java.util.List;

import vn.hoidanit.jobhunter.domain.Skill;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    public Boolean existsByName(String name);

    public List<Skill> findByIdIn(List<Long> id);
}
