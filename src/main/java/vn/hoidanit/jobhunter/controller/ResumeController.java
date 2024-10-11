package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createNewResume(@Valid @RequestBody Resume dataResume)
            throws IdInvalidException {
        // check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(dataResume);
        if (!isIdExist) {
            throw new IdInvalidException("User or job not found!");
        }

        ResCreateResumeDTO newResume = this.resumeService.handleCreateResume(dataResume);
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume dataResume)
            throws IdInvalidException {
        boolean isExist = this.resumeService.checkExistsResume(dataResume.getId());
        if (!isExist) {
            throw new IdInvalidException("Resume with id = " + dataResume.getId() + " not found!");
        }

        ResUpdateResumeDTO currentResume = this.resumeService.handleUpdateStatusResume(dataResume);
        return ResponseEntity.status(HttpStatus.OK).body(currentResume);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        boolean isExist = this.resumeService.checkExistsResume(id);
        if (!isExist) {
            throw new IdInvalidException("Resume with id = " + id + " not found!");
        }

        this.resumeService.deleteResumeById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchResumeById(id);
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume with id = " + id + " not found!");
        }

        ResFetchResumeDTO fetchResume = this.resumeService.fetchResume(resumeOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(fetchResume);
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(
            Pageable pageable, @Filter Specification<Resume> spec) {
        ResultPaginationDTO dataResumes = this.resumeService.fetchAllResumes(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(dataResumes);
    }

}
