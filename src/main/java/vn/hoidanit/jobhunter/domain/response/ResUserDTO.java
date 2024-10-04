package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
