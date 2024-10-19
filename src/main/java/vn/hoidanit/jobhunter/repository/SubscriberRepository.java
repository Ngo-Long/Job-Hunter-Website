package vn.hoidanit.jobhunter.repository;

import vn.hoidanit.jobhunter.domain.Subscriber;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>,
                JpaSpecificationExecutor<Subscriber> {
        boolean existsByEmail(String email);

        Subscriber findByEmail(String email);
}
