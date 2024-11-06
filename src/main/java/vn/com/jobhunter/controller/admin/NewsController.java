package vn.com.jobhunter.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;

import vn.com.jobhunter.domain.News;
import vn.com.jobhunter.domain.response.ResultPaginationDTO;
import vn.com.jobhunter.domain.response.news.ResCreateNewsDTO;
import vn.com.jobhunter.domain.response.news.ResFetchNewsDTO;
import vn.com.jobhunter.domain.response.news.ResUpdateNewsDTO;
import vn.com.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.com.jobhunter.service.NewsService;
import vn.com.jobhunter.util.annotation.ApiMessage;
import vn.com.jobhunter.util.constant.NewsStateEnum;
import vn.com.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class NewsController {

    private NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/news")
    @ApiMessage("Create a new news")
    public ResponseEntity<ResCreateNewsDTO> createNews(@Valid @RequestBody News dataNews)
            throws IdInvalidException {
        boolean isTitleExist = this.newsService.isTitleExist(dataNews.getTitle());
        if (isTitleExist) {
            throw new IdInvalidException("Tiêu đề đã tồn tại, vui lòng sử dụng tiêu đề khác!");
        }

        ResCreateNewsDTO newNews = this.newsService.handleCreateNews(dataNews);
        return ResponseEntity.status(HttpStatus.CREATED).body(newNews);
    }

    @PutMapping("/news")
    public ResponseEntity<ResUpdateNewsDTO> updateNews(@RequestBody News dataNews) throws IdInvalidException {
        News currentNews = this.newsService.fetchNewsById(dataNews.getId());
        if (currentNews == null) {
            throw new IdInvalidException("Không tìm thấy bài tin tức!");
        }

        boolean isTitleExist = this.newsService.isTitleExist(dataNews.getTitle());
        if (isTitleExist && currentNews.getTitle() == dataNews.getTitle()) {
            throw new IdInvalidException("Tiêu đề đã tồn tại, vui lòng sử dụng tiêu đề khác!");
        }

        ResUpdateNewsDTO updateNews = this.newsService.handleUpdateNews(dataNews);
        return ResponseEntity.ok(updateNews);
    }

    @DeleteMapping("/news/{id}")
    @ApiMessage("Delete a news")
    public ResponseEntity<Void> deleteNews(@PathVariable("id") long id)
            throws IdInvalidException {
        if (this.newsService.fetchNewsById(id) == null) {
            throw new IdInvalidException("Tiêu đề không tồn tại!");
        }

        this.newsService.handleDeleteNews(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/news/{id}")
    @ApiMessage("Fetch news by id")
    public ResponseEntity<ResFetchNewsDTO> getNewsById(@PathVariable("id") long id) throws IdInvalidException {
        News dataNews = this.newsService.fetchNewsById(id);
        if (dataNews == null) {
            throw new IdInvalidException("Tiêu đề không tồn tại!");
        }

        return ResponseEntity.ok(this.newsService.convertToResNewsDTO(dataNews));
    }

    @GetMapping("/news")
    @ApiMessage("Fetch all news")
    public ResponseEntity<ResultPaginationDTO> getNewsList(
            Pageable pageable,
            @Filter Specification<News> spec) {
        return ResponseEntity.ok(this.newsService.handleFetchNewsList(spec, pageable));
    }
}
