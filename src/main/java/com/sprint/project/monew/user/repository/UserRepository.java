package com.sprint.project.monew.user.repository;

import com.sprint.project.monew.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
