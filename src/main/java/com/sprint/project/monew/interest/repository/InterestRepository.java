package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

  @Query("""
    SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
    FROM Interest i
    WHERE LOWER(i.name) LIKE CONCAT('%', LOWER(:name), '%')
""")
  boolean existsByNameSimilarity(String name);
}