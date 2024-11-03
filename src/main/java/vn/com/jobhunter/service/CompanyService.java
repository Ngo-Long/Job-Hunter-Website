package vn.com.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.com.jobhunter.domain.Company;
import vn.com.jobhunter.domain.User;
import vn.com.jobhunter.domain.response.ResultPaginationDTO;
import vn.com.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.com.jobhunter.repository.CompanyRepository;
import vn.com.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public CompanyService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();

            // fetch all user belong to this company
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }

    public ResultPaginationDTO handleFetchCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());

        return rs;
    }

    public Optional<Company> fetchCompanyOptionalById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional;
        }

        return null;
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }

        return null;
    }

    public Company handleUpdateCompany(Company reqCompany) {
        Company currentCompany = this.fetchCompanyById(reqCompany.getId());
        if (currentCompany == null) {
            return null;
        }

        currentCompany.setName(reqCompany.getName());
        currentCompany.setLogo(reqCompany.getLogo());
        currentCompany.setAddress(reqCompany.getAddress());
        currentCompany.setDescription(reqCompany.getDescription());

        return this.companyRepository.save(currentCompany);
    }

    public List<Company> fetchCompanyByName(String name) {
        return this.companyRepository.findByName(name);
    }
}
