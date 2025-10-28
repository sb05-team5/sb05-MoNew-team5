package com.sprint.project.monew.user.repository;

import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBatchRepository extends JpaRepository<User, UUID> {

  @Query("""
      select u from User u 
      where u.deletedAt is not NULL and u.deletedAt < :threshold
      """)
  List<User> findAllMarkedDeletion(@Param("threshold") Instant threshold);

}
