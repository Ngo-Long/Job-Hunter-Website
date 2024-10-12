package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ResCreateResumeDTO handleCreateResume(Resume dataResume) {
        // create resume
        this.resumeRepository.save(dataResume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(dataResume.getId());
        res.setCreateBy(dataResume.getCreatedBy());
        res.setCreateAt(dataResume.getCreatedAt());

        return res;
    }

    public ResUpdateResumeDTO handleUpdateStatusResume(Resume dataResume) {
        Resume currentResume = this.resumeRepository.findById(dataResume.getId()).get();
        if (currentResume == null) {
            return null;
        }

        // update resume
        currentResume.setStatus(dataResume.getStatus());
        this.resumeRepository.save(currentResume);

        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdateBy(currentResume.getUpdatedBy());
        res.setUpdateAt(currentResume.getUpdatedAt());

        return res;
    }

    public ResFetchResumeDTO fetchResume(Resume dataResume) {
        // create job resume DTO
        ResFetchResumeDTO.JobResume jobResumeDTO = new ResFetchResumeDTO.JobResume();
        jobResumeDTO.setId(dataResume.getJob().getId());
        jobResumeDTO.setName(dataResume.getJob().getName());

        // create user resume DTO
        ResFetchResumeDTO.UserResume userResumeDTO = new ResFetchResumeDTO.UserResume();
        userResumeDTO.setId(dataResume.getUser().getId());
        userResumeDTO.setName(dataResume.getUser().getName());

        // create resume DTO
        ResFetchResumeDTO resumeDTO = new ResFetchResumeDTO();
        resumeDTO.setId(dataResume.getId());
        resumeDTO.setUrl(dataResume.getUrl());
        resumeDTO.setEmail(dataResume.getEmail());
        resumeDTO.setStatus(dataResume.getStatus());

        resumeDTO.setCreatedAt(dataResume.getCreatedAt());
        resumeDTO.setCreatedBy(dataResume.getCreatedBy());
        resumeDTO.setUpdatedAt(dataResume.getUpdatedAt());
        resumeDTO.setUpdatedBy(dataResume.getUpdatedBy());

        if (dataResume.getJob().getCompany() != null) {
            resumeDTO.setCompanyName(dataResume.getJob().getCompany().getName());
        }

        resumeDTO.setJob(jobResumeDTO);
        resumeDTO.setUser(userResumeDTO);

        return resumeDTO;
    }

    public void deleteResumeById(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.fetchResume(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }

    public boolean checkExistsResume(long id) {
        return this.resumeRepository.existsById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null) {
            return false;
        }

        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty()) {
            return false;
        }

        if (resume.getJob() == null) {
            return false;
        }

        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty()) {
            return false;
        }

        return true;
    }

    public Optional<Resume> fetchResumeById(long id) {
        return this.resumeRepository.findById(id);
    }
}
