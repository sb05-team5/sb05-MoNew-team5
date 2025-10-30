package com.sprint.project.monew.interest.service;

import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.repository.InterestRepository;
import com.sprint.project.monew.interest.dto.SubscriptionDto;
import com.sprint.project.monew.interest.entity.Subscription;
import com.sprint.project.monew.interest.mapper.SubscriptionMapper;
import com.sprint.project.monew.interest.repository.SubscriptionRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final InterestRepository interestRepository;
  private final SubscriptionMapper subscriptionMapper;

  @Transactional
  public SubscriptionDto create(UUID userId, UUID interestId) {
    User user = validatedUserId(userId);
    Interest interest = validatedInterest(interestId);
    isSubscribed(user, interest);

    Subscription subscription = new Subscription(interest, user);
    subscriptionRepository.save(subscription);

    interest.increaseSubscriber();

    return subscriptionMapper.toSubscriptionDto(subscription);
  }

  @Transactional
  public void delete(UUID userId, UUID interestId) {
    User user = validatedUserId(userId);
    Interest interest = validatedInterest(interestId);
    validatedSubscription(user, interest);

    interest.decreaseSubscriber();

    subscriptionRepository.deleteByUserAndInterest(user, interest);
  }

  private User validatedUserId(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
  }

  private Interest validatedInterest(UUID interestId) {
    return interestRepository.findById(interestId)
        .orElseThrow(() -> new NoSuchElementException("관심사가 존재하지 않습니다."));
  }

  private void isSubscribed(User user, Interest interest) {
    if (subscriptionRepository.existsByUserAndInterest(user, interest)) {
      throw new IllegalArgumentException("이미 구독 중입니다.");
    }
  }

  private void validatedSubscription(User user, Interest interest) {
    if (!subscriptionRepository.existsByUserAndInterest(user, interest)) {
      throw new IllegalArgumentException("구독 내역이 존재하지 않습니다.");
    }
  }
}
