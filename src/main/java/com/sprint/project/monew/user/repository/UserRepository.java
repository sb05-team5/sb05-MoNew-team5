package com.sprint.project.monew.user.repository;

import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  Optional<User> findByIdAndDeletedAtIsNull(UUID uuid);


    @Query("select u.nickname from User u where u.id = :userId")
    Optional<String> findNicknameById(@Param("userId") UUID userId);

  @Modifying
  @Query("""
          update User u 
          set u.deletedAt = :now 
          where u.id = :id
      """)
  void deleteByIdForSoft(@Param("id") UUID id, @Param("now") Instant now);
}
