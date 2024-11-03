package vn.com.jobhunter.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.com.jobhunter.domain.Skill;
import vn.com.jobhunter.domain.response.ResultPaginationDTO;
import vn.com.jobhunter.repository.SkillRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isExistsName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill dataSkill) {
        return this.skillRepository.save(dataSkill);
    }

    public Skill handleUpdateSkill(Skill dataSkill) {
        Skill currentSkill = this.fetchSkillById(dataSkill.getId());
        if (currentSkill == null) {
            return null;
        }

        currentSkill.setName(dataSkill.getName());
        return this.skillRepository.save(currentSkill);
    }

    public void handleDeleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete subscriber (inside subscriber_skill table)
        currentSkill.getSubscribers().forEach(subs -> subs.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.deleteById(id);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }

        return null;
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

}
