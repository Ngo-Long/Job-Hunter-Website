package vn.com.jobhunter.domain.response.resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateResumeDTO {

    private String UpdateBy;
    private Instant UpdateAt;
}
