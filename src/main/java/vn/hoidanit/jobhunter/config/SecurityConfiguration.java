package vn.hoidanit.jobhunter.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.util.Base64;

import jakarta.persistence.Converts;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${jobhunter.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    /**
     * The jwtAuthenticationConverter method configures how Spring Security converts
     * permissions from a JWT token, getting the permission from the "permission"
     * field in the token and not prefixing the permission.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * Configures a security filter chain that applies to incoming HTTP requests
     * to determine whether security should be enforced for the request.
     *
     * - Disables CSRF (Cross-Site Request Forgery) protection since the application
     * may primarily expose APIs that are not susceptible to CSRF attacks.
     * - Enables CORS (Cross-Origin Resource Sharing) to allow requests from
     * different origins
     * with default settings.
     * - Configures HTTP request authorization using `authorizeHttpRequests`, which
     * allows
     * public access to certain paths (whitelisted) while securing other routes.
     * - Uses `oauth2ResourceServer` with JWT support for resource protection,
     * ensuring
     * that users must provide valid JWT tokens to access secured resources.
     * - Disables the default form-based login provided by Spring Security since
     * authentication is handled via OAuth2 and JWT.
     * - Configures session management to be stateless
     * (`SessionCreationPolicy.STATELESS`),
     * meaning the application does not maintain server-side sessions for each user,
     * which is ideal for stateless REST APIs.
     *
     * @param http                           The HttpSecurity object used to
     *                                       configure HTTP security settings.
     * @param customAuthenticationEntryPoint The entry point used to handle
     *                                       authentication errors, such as when
     *                                       a user attempts to access a secured
     *                                       resource
     *                                       without proper authentication.
     *
     * @return SecurityFilterChain The security configuration object that defines
     *         the filters applied to HTTP requests.
     * @throws Exception If an error occurs during the security configuration
     *                   process.
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        String[] whiteList = {
                "/", "/storage/**", "/api/v1/email/**",
                "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/register",
                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
        };

        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(whiteList).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/companies/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/jobs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/skills/**").permitAll()
                                .anyRequest().authenticated())

                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint))

                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
