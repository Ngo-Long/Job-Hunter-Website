package vn.com.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Setter
@Getter
public class ResCreateUserDTO {

    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
