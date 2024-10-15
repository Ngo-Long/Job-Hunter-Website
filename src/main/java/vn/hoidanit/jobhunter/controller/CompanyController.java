package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company reqCompany) {
        Company newCompany = this.companyService.handleCreateCompany(reqCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getCompanies(
            Pageable pageable,
            @Filter Specification<Company> spec) {
        return ResponseEntity.ok(this.companyService.handleFetchCompanies(spec, pageable));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompanyById(@PathVariable("id") Long id) throws IdInvalidException {
        Company currentCompany = this.companyService.fetchCompanyById(id);
        if (currentCompany == null) {
            throw new IdInvalidException("Company with id = " + id + " not found!");
        }

        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Company currentCompany = this.companyService.fetchCompanyById(id);
        if (currentCompany == null) {
            throw new IdInvalidException("Company with id = " + id + " not found!");
        }

        Company dataCompany = this.companyService.fetchCompanyById(id);
        return ResponseEntity.ok().body(dataCompany);
    }

    @PutMapping("companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
        Company dataCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok().body(dataCompany);
    }

}
