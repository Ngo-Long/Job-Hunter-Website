package vn.hoidanit.jobhunter.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
