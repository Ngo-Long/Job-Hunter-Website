package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;

@Controller
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job dataJob) {
        ResCreateJobDTO newJob = this.jobService.handleCreateJob(dataJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job dataJob) throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(dataJob.getId());
        if (currentJob == null) {
            throw new IdInvalidException("Job not found!");
        }

        ResUpdateJobDTO newJob = this.jobService.handleUpdateJob(dataJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<String> deleteJobById(@PathVariable("id") Long id) throws IdInvalidException {
        Job dataJob = this.jobService.fetchJobById(id);
        if (dataJob == null) {
            throw new IdInvalidException("Job not found!");
        }

        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) throws IdInvalidException {
        Job fetchJob = this.jobService.fetchJobById(id);
        if (fetchJob == null) {
            throw new IdInvalidException("Job not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(fetchJob);
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            Pageable pageable,
            @Filter Specification<Job> spec) {
        ResultPaginationDTO dataJobs = this.jobService.fetchAllJobs(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(dataJobs);
    }
}
