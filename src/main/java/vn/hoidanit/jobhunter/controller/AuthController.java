package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        private final UserService userService;
        private final SecurityUtil securityUtil;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;

        public AuthController(
                        UserService userService,
                        SecurityUtil securityUtil,
                        AuthenticationManagerBuilder authenticationManagerBuilder) {
                this.userService = userService;
                this.securityUtil = securityUtil;
                this.authenticationManagerBuilder = authenticationManagerBuilder;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // create new resLoginDto
                ResLoginDTO resLoginDto = new ResLoginDTO();

                // create new userLogin (userLogin is a subclass of the inner class resLoginDto)
                User currentUserDB = this.userService.fetchUserByUsername(loginDto.getUsername());

                // set user login
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                currentUserDB.getId(),
                                currentUserDB.getEmail(),
                                currentUserDB.getName());
                resLoginDto.setUser(userLogin);

                // set access token
                String access_token = this.securityUtil.createAccessToken(authentication, resLoginDto);
                resLoginDto.setAccessToken(access_token);

                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), resLoginDto);

                // update user
                this.userService.updateUserToken(refresh_token, loginDto.getUsername());

                // set cookies
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(resLoginDto);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Fetch a account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.fetchUserByUsername(email);

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getEmail());
                        userLogin.setName(currentUserDB.getName());
                }

                return ResponseEntity.ok().body(userLogin);
        }
}
