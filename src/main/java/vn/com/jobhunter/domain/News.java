package vn.com.jobhunter.domain;

import lombok.Getter;
import lombok.Setter;
import vn.com.jobhunter.util.SecurityUtil;
import vn.com.jobhunter.util.constant.NewsStateEnum;

import java.time.Instant;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.validation.constraints.NotBlank;

@Table(name = "news")
@Entity
@Getter
@Setter
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @NotBlank(message = "Phân loại không được để trống")
    private String category;

    private String author;
    private String image;
    private int views;

    @Enumerated(EnumType.STRING)
    private NewsStateEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }

}
