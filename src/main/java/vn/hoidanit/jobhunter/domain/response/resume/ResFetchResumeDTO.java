package vn.hoidanit.jobhunter.domain.response.resume;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeStateEnum;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@Setter
public class ResFetchResumeDTO {

    private long id;
    private String url;
    private String email;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private JobResume job;
    private UserResume user;

    @Getter
    @Setter
    public static class JobResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class UserResume {
        private long id;
        private String name;
    }

}
