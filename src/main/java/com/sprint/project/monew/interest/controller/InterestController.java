package com.sprint.project.monew.interest.controller;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;
import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.dto.InterestUpdateRequest;
import com.sprint.project.monew.interest.service.InterestService;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/interests")
public class InterestController {

  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> create(@RequestBody InterestRegisterRequest req) {
    InterestDto created = interestService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<InterestDto>> findAll(@ModelAttribute InterestQuery query) {
    CursorPageResponse<InterestDto> interests = interestService.findAll(query);
    return ResponseEntity.status(HttpStatus.OK).body(interests);
  }

  @PatchMapping("{interestId}")
  public ResponseEntity<InterestDto> update(
      @PathVariable UUID interestId, @RequestBody InterestUpdateRequest req) {
    InterestDto updated = interestService.update(interestId, req);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("{interestId}")
  public ResponseEntity<InterestDto> delete(@PathVariable UUID interestId) {
    interestService.delete(interestId);
    return ResponseEntity.noContent().build();
  }

}
