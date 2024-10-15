package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User dataUser) {
        // check company
        if (dataUser.getCompany() != null) {
            Company dataCompany = this.companyService.fetchCompanyById(dataUser.getCompany().getId());
            dataUser.setCompany(dataCompany != null ? dataCompany : null);
        }

        // check role
        if (dataUser.getRole() != null) {
            Role dataRole = this.roleService.fetchRoleById(dataUser.getRole().getId());
            dataUser.setRole(dataRole != null ? dataRole : null);
        }

        return this.userRepository.save(dataUser);
    }

    public User handleUpdateUser(User dataUser) {
        User currentUser = this.fetchUserById(dataUser.getId());
        if (currentUser == null) {
            return null;
        }

        // check company
        if (dataUser.getCompany() != null) {
            Company dataCompany = this.companyService.fetchCompanyById(dataUser.getCompany().getId());
            currentUser.setCompany(dataCompany != null ? dataCompany : null);
        }

        // check role
        if (dataUser.getRole() != null) {
            Role dataRole = this.roleService.fetchRoleById(dataUser.getRole().getId());
            currentUser.setRole(dataRole != null ? dataRole : null);
        }

        currentUser.setName(dataUser.getName());
        currentUser.setAge(dataUser.getAge());
        currentUser.setGender(dataUser.getGender());
        currentUser.setAddress(dataUser.getAddress());

        return this.userRepository.save(currentUser);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return null;
    }

    public ResultPaginationDTO handleFetchUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User fetchUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public Boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        // create response user DTO
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        // create response company user DTO
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        // create response role user DTO
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        return res;
    }

    public void updateUserToken(String email, String token) {
        User currentUser = this.fetchUserByUsername(email);
        if (currentUser == null) {
            return;
        }

        currentUser.setRefreshToken(token);
        this.userRepository.save(currentUser);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
