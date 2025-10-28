package com.sprint.project.monew.user.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, unique = true, length = 20)
  private String nickname;

  @Column(nullable = false, length = 100)
  private String password;

  @Column
  private Instant deletedAt;


  public User(String email, String nickname, String password) {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
  }

  public void changeNickname(String newNickname) {
    this.nickname = newNickname;
  }
}
