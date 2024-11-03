package vn.com.jobhunter.controller.admin;

import jakarta.validation.Valid;
import vn.com.jobhunter.domain.Skill;
import vn.com.jobhunter.domain.response.ResultPaginationDTO;
import vn.com.jobhunter.service.SkillService;
import vn.com.jobhunter.util.annotation.ApiMessage;
import vn.com.jobhunter.util.error.IdInvalidException;

import com.turkraft.springfilter.boot.Filter;

import org.springframework.stereotype.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill dataSkill) throws IdInvalidException {
        boolean isNameExists = this.skillService.isExistsName(dataSkill.getName());
        if (isNameExists) {
            throw new IdInvalidException("Tên " + dataSkill.getName() + " đã tồn tại. Vui lòng nhập tên khác!");
        }

        Skill newSkill = this.skillService.handleCreateSkill(dataSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill dataSkill) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(dataSkill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Mã skill " + dataSkill.getId() + " không tồn tại!");
        }

        boolean isNameExists = this.skillService.isExistsName(dataSkill.getName());
        if (isNameExists) {
            throw new IdInvalidException("Tên " + dataSkill.getName() + " đã tồn tại. Vui lòng nhập tên khác!");
        }

        return ResponseEntity.ok(this.skillService.handleUpdateSkill(dataSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<String> deleteSkillById(@PathVariable("id") Long id) throws IdInvalidException {
        Skill dataSkill = this.skillService.fetchSkillById(id);
        if (dataSkill == null) {
            throw new IdInvalidException("Skill không tồn tại!");
        }

        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Fetch skill by id")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) throws IdInvalidException {
        Skill dataSkill = this.skillService.fetchSkillById(id);
        if (dataSkill == null) {
            throw new IdInvalidException("Skill dùng không tồn tại!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(dataSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            Pageable pageable,
            @Filter Specification<Skill> spec) {
        ResultPaginationDTO dataSkills = this.skillService.fetchAllSkills(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(dataSkills);
    }

}
