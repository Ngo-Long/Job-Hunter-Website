package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;

import vn.hoidanit.jobhunter.service.UserService;

import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.oauth2.jwt.Jwt;
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

        public ResponseEntity<ResLoginDTO> createLoginResponse(String email, User currentUserDB) {
                if (email == null || currentUserDB == null) {
                        return null;
                }

                // issue new token/set refresh token as cookies
                ResLoginDTO resLoginDto = new ResLoginDTO();

                // create new userLogin (userLogin is a subclass of the inner class resLoginDto)
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                currentUserDB.getId(),
                                currentUserDB.getEmail(),
                                currentUserDB.getName());

                // create access token
                String access_token = this.securityUtil.createAccessToken(email, userLogin);

                // set resLoginDto
                resLoginDto.setUser(userLogin);
                resLoginDto.setAccessToken(access_token);

                // update refresh token for user
                String new_refresh_token = this.securityUtil.createRefreshToken(email, userLogin);
                this.userService.updateUserToken(email, new_refresh_token);

                // create cookies for refresh token
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(resLoginDto);
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) throws IdInvalidException {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // get data user login
                User currentUserDB = this.userService.fetchUserByUsername(loginDto.getUsername());
                if (currentUserDB == null) {
                        throw new IdInvalidException("Refresh Token không hợp lệ");
                }

                return createLoginResponse(authentication.getName(), currentUserDB);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Fetch user by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("")) {
                        throw new IdInvalidException("Bạn không có refresh token ở cookie");
                }

                // check valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User currentUserDB = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUserDB == null) {
                        throw new IdInvalidException("Refresh Token không hợp lệ");
                }

                return createLoginResponse(email, currentUserDB);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Fetch a account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.fetchUserByUsername(email);
                if (currentUserDB == null) {
                        throw new IdInvalidException("Không tìm thấy người dùng!");
                }

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                userLogin.setId(currentUserDB.getId());
                userLogin.setEmail(currentUserDB.getEmail());
                userLogin.setName(currentUserDB.getName());

                return ResponseEntity.ok().body(userLogin);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout user")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                if (email.equals("")) {
                        throw new IdInvalidException("Access Token không hợp lệ!");
                }

                // update refrech token == null
                this.userService.updateUserToken(email, null);

                // remove refresh token cookie
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }
}
