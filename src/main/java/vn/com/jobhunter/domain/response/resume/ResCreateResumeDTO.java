package vn.com.jobhunter.domain.response.resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateResumeDTO {

    private long id;
    private String CreateBy;
    private Instant CreateAt;
}
