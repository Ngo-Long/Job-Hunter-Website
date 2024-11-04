package vn.com.jobhunter.domain.response.news;

import lombok.Getter;
import lombok.Setter;
import vn.com.jobhunter.util.constant.NewsStateEnum;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@Setter
public class ResUpdateNewsDTO {

	private long id;
    private String title;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;    
    
    private int views;
    private String author;
    private String image;
    private String category;
    
    @Enumerated(EnumType.STRING)
    private NewsStateEnum status;
        
    private Instant updatedAt;
    private String updatedBy;
}
