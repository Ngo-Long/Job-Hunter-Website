package vn.hoidanit.jobhunter.config;

import java.util.Optional;
import java.io.IOException;

import vn.hoidanit.jobhunter.domain.response.ResRestDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;

/**
 * AuthenticationEntryPoint is a way for your Spring application to react
 * when someone tries to access a part of your site or API without the
 * necessary permissions. It's like a doorkeeper that decides what to do
 * with visitors who still need an invite to the party.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        ResRestDTO<Object> res = new ResRestDTO<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = Optional.ofNullable(authException.getCause()) // NULL
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());
        res.setError(errorMessage);

        res.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...");

        mapper.writeValue(response.getWriter(), res);
    }
}