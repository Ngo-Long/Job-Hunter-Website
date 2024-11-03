package vn.com.jobhunter.config;

import java.util.Collections;
import org.springframework.stereotype.Component;

import vn.com.jobhunter.service.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The UserDetailsService is a cornerstone of user management in Spring
 * Security, providing a seamless bridge between your user data and Spring
 * Security's authentication mechanism. By implementing a custom
 * UserDetailsService, developers gain fine-grained control over loading user
 * details, enabling robust and flexible authentication workflows.
 */
@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.com.jobhunter.domain.User user = this.userService.fetchUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
