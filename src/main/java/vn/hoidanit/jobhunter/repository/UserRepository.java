package vn.hoidanit.jobhunter.repository;

import vn.hoidanit.jobhunter.domain.User;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}