package vn.hoidanit.jobhunter.controller.admin;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User dataUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(dataUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + dataUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác!");
        }

        String hashPassword = this.passwordEncoder.encode(dataUser.getPassword());
        dataUser.setPassword(hashPassword);

        User newUser = this.userService.handleCreateUser(dataUser);
        ResCreateUserDTO res = this.userService.convertToResCreateUserDTO(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User dataUser) throws IdInvalidException {
        User updateUser = this.userService.handleUpdateUser(dataUser);
        if (updateUser == null) {
            throw new IdInvalidException("Người dùng không tồn tại!");
        }

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updateUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("Người dùng không tồn tại!");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("Người dùng không tồn tại!");
        }

        return ResponseEntity.ok(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getUsers(
            Pageable pageable,
            @Filter Specification<User> spec) {
        return ResponseEntity.ok(this.userService.handleFetchUsers(spec, pageable));
    }

}
