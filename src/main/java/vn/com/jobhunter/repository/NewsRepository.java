package vn.com.jobhunter.repository;

import java.util.List;

import vn.com.jobhunter.domain.News;
import vn.com.jobhunter.domain.User;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
	Boolean existsByTitle(String title);
	
	List<News> findByTitle(String title);
   
}
