package vn.hoidanit.jobhunter.domain.response.job;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.Instant;

import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResUpdateJobDTO {

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
    private Instant updatedAt;
    private String updatedBy;
}
