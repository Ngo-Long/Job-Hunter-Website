package vn.com.jobhunter.domain.response.job;

import lombok.Getter;
import lombok.Setter;
import vn.com.jobhunter.util.constant.LevelEnum;

import java.util.List;
import java.time.Instant;

@Getter
@Setter
public class ResCreateJobDTO {

    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private boolean isActive;
    private List<String> skills;

    private Instant startDate;
    private Instant endDate;
    private Instant createdAt;
    private String createdBy;
}
