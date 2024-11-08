package vn.com.jobhunter.config;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.com.jobhunter.domain.Permission;
import vn.com.jobhunter.domain.Role;
import vn.com.jobhunter.domain.User;
import vn.com.jobhunter.service.UserService;
import vn.com.jobhunter.util.SecurityUtil;
import vn.com.jobhunter.util.error.PermissionException;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * HandlerInterceptors are part of the Spring MVC framework
 * and sit between the DispatcherServlet and our Controllers.
 * It can intercept requests before they reach our controllers,
 * and before and after the view is rendered.
 */
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email != null && !email.isEmpty()) {
            User user = this.userService.fetchUserByUsername(email);

            if (user != null) {
                Role role = user.getRole();

                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    // if (isAllow == false) {
                    // throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                    // }

                } else {
                    // throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }
            }
        }

        return true;
    }
}
