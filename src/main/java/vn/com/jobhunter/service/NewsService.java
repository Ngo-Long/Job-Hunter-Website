package vn.com.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.com.jobhunter.domain.News;
import vn.com.jobhunter.domain.User;
import vn.com.jobhunter.domain.response.ResUserDTO;
import vn.com.jobhunter.domain.response.ResultPaginationDTO;
import vn.com.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.com.jobhunter.domain.response.news.ResCreateNewsDTO;
import vn.com.jobhunter.domain.response.news.ResFetchNewsDTO;
import vn.com.jobhunter.domain.response.news.ResUpdateNewsDTO;
import vn.com.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.com.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.com.jobhunter.repository.NewsRepository;

@Service
public class NewsService {

	private final NewsRepository newsRepository;
	
	public NewsService(NewsRepository newsRepository) {
		this.newsRepository = newsRepository;
	}
	
	 public ResCreateNewsDTO handleCreateNews(News news) {
		this.newsRepository.save(news);
		 
		ResCreateNewsDTO res = new ResCreateNewsDTO();
        res.setId(news.getId());
        res.setTitle(news.getTitle());
        res.setContent(news.getContent());
        res.setAuthor(news.getAuthor());
        res.setImage(news.getImage());
        res.setCategory(news.getCategory());
        res.setStatus(news.getStatus());
        res.setCreateAt(news.getCreatedAt());
        res.setCreatedBy(news.getCreatedBy());

        return res;
	 }

	 public void handleDeleteNews(long id) {
        this.newsRepository.deleteById(id);
	 }

	 

	 public Optional<News> fetchNewsOptionalById(long id) {
        Optional<News> newsOptional = this.newsRepository.findById(id);
        if (newsOptional.isPresent()) {
            return newsOptional;
        }

        return null;
 	  }

	 public News fetchNewsById(long id) {
		Optional<News> newsOptional = this.newsRepository.findById(id);
        if (newsOptional.isPresent()) {
            return newsOptional.get();
        }

        return null;
	 }

	 public ResUpdateNewsDTO handleUpdateNews(News reqNews) {
    	News currentNews = this.fetchNewsById(reqNews.getId());
        if (currentNews == null) {
            return null;
        }

        this.newsRepository.save(reqNews);
        
        ResUpdateNewsDTO res = new ResUpdateNewsDTO();
        res.setTitle(currentNews.getTitle());
        res.setContent(currentNews.getContent());
        res.setAuthor(currentNews.getAuthor());
        res.setImage(currentNews.getImage());
        res.setCategory(currentNews.getCategory());
        res.setStatus(currentNews.getStatus());
        res.setUpdatedAt(currentNews.getUpdatedAt());
        res.setUpdatedBy(currentNews.getUpdatedBy());

        return res;
	 }

	 public List<News> fetchNewsByTitle(String news) {
		 return this.newsRepository.findByTitle(news);
	 }
	 
	 public Boolean isTitleExist(String title) {
		 return this.newsRepository.existsByTitle(title);
	 }
	 
	 public ResFetchNewsDTO convertToResNewsDTO(News news) {
         // create fetch response news DTO
		 ResFetchNewsDTO res = new ResFetchNewsDTO();
         res.setId(news.getId());	        
         res.setTitle(news.getTitle());
         res.setViews(news.getViews());
         res.setStatus(news.getStatus());
         res.setAuthor(news.getAuthor());
         res.setContent(news.getContent());		        
         res.setCategory(news.getCategory());
         res.setCreateAt(news.getCreatedAt());
         res.setCreatedBy(news.getCreatedBy());
         res.setUpdatedAt(news.getUpdatedAt());
         res.setUpdatedBy(news.getUpdatedBy());	        

        return res;
	 }
	 
	 public ResultPaginationDTO handleFetchNewsList(Specification<News> spec, Pageable pageable) {        
         Page<News> pageNews = this.newsRepository.findAll(spec, pageable);
         ResultPaginationDTO rs = new ResultPaginationDTO();
         ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

         meta.setPage(pageable.getPageNumber() + 1);
         meta.setPageSize(pageable.getPageSize());

         meta.setPages(pageNews.getTotalPages());
         meta.setTotal(pageNews.getTotalElements());
 
         rs.setMeta(meta);
        
         List<ResFetchNewsDTO> listNews = pageNews.getContent()
                .stream().map(item -> this.convertToResNewsDTO(item))
                .collect(Collectors.toList());

         rs.setResult(listNews);       

         return rs;
	 }
}
