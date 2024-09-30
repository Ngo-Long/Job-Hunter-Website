package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
public class ResLoginDTO {

    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {

        private long id;
        private String email;
        private String name;

    }
}
