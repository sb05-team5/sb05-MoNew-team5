package com.sprint.project.monew.user.repository;

import com.sprint.project.monew.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
