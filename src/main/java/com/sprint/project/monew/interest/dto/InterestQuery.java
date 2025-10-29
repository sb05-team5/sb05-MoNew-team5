package com.sprint.project.monew.interest.dto;

public record InterestQuery(
    String name,
    String keyword,
    String idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {
  public InterestQuery {
    if (size == null) {
      size = 10;
    }
    if (sortField == null) {
      sortField = "name";
    }
    if (sortDirection == null) {
      sortDirection = "asc";
    }
  }

}
