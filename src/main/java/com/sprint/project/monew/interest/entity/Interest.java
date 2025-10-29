package com.sprint.project.monew.interest.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  @Column(nullable = false)
  private String name;

  @Column(name = "subscriber_Count", nullable = false)
  private long subscriberCount = 0L;

  @Column(nullable = false)
  private String keyword;

  public Interest(String name, String keyword) {
    this.name = name;
    this.keyword = keyword;
  }

  public List<String> getKeywords() {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }

    return Arrays.asList(keyword.split(","));
  }

  public void update(List<String> keywords) {
    this.keyword = String.join(",", keywords);
  }

  public void increaseSubscriber() {
    this.subscriberCount++;
  }

  public void decreaseSubscriber() {
    if (this.subscriberCount < 0) {
      throw new IllegalArgumentException("구독자 수는 0보다 작을 수 없습니다.");
    }
    this.subscriberCount--;
  }
}
