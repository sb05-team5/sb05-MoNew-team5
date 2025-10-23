package com.sprint.project.monew.interest.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "interests")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Interest extends BaseEntity {

  @Column(nullable = false, updatable = false)
  private String name;

  @Column(name = "subscriber_Count", nullable = false)
  private long subscriberCount = 0L;

  @Column(nullable = false)
  private String keyword;

  public Interest(String name, String keyword) {
    this.name = name;
    this.keyword = keyword;
  }
}
