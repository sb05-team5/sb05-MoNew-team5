package com.sprint.project.monew.interest.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Interest extends BaseEntity {

  @Column(nullable = false, updatable = false)
  private String name;

  @Column(name = "subscriber_Count", nullable = false)
  private int subscriberCount;

  @Column(nullable = false)
  private String keyword;

  public void update(String keyword) {
    if (keyword != null && keyword.equals(this.keyword)) {
      this.keyword = keyword;
    }
  }
}
