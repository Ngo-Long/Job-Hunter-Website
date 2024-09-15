package vn.hoidanit.jobhunter.controller;

import java.util.List;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User dataUser = this.userService.fetchUserById(id);
        return ResponseEntity.ok(dataUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> dataUsers = this.userService.fetchAllUsers();
        return ResponseEntity.ok(dataUsers);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User postUser) {
        User newUser = this.userService.handleCreateUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User dataUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(dataUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id > 1500) {
            throw new IdInvalidException("Id khong lon hon 1500");
        }

        this.userService.deleteUserById(id);
        return ResponseEntity.ok("dataUser");
        // return ResponseEntity.status(HttpStatus.OK).body("dataUser");
    }

}
