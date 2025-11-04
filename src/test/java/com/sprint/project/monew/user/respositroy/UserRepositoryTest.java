package com.sprint.project.monew.user.respositroy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.project.monew.config.TestRepositoryConfig;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@Import(TestRepositoryConfig.class)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager em;

  private User createTestUser(String email, String nickname) {
    return User.builder()
        .email(email)
        .nickname(nickname)
        .password("qwer1234!")
        .createdAt(Instant.now())
        .build();
  }

  @Test
  @DisplayName("사용자를 저장하고 이메일로 조회")
  void saveAndFindUser() {
    User user = createTestUser("test@email.com", "테스트유저");
    userRepository.save(user);

    em.flush();
    em.clear();

    Optional<User> foundUser = userRepository.findByEmailAndDeletedAtIsNull("test@email.com");

    assertAll(
        () -> assertThat(foundUser).isPresent(),
        () -> assertThat(foundUser.get().getNickname()).isEqualTo("테스트유저"),
        () -> assertThat(foundUser.get().getDeletedAt()).isNull()
    );
  }

  @Test
  @DisplayName("존재하는 이메일로 존재 여부 확인 시 true를 반환한다")
  void existsByEmail_ExistingEmail_ReturnsTrue() {
    User user = createTestUser("exists@email.com", "닉네임1");
    userRepository.save(user);

    boolean exists = userRepository.existsByEmail("exists@email.com");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 확인 시 false를 반환한다")
  void existsByEmail_NonExistingEmail_ReturnsFalse() {
    boolean exists = userRepository.existsByEmail("no@email.com");
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("존재하는 닉네임으로 존재 여부 확인 시 true를 반환한다")
  void existsByNickname_ExistingNickname_ReturnsTrue() {
    User user = createTestUser("nick@email.com", "닉네임중복");
    userRepository.save(user);

    boolean exists = userRepository.existsByNickname("닉네임중복");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 닉네임으로 확인 시 false를 반환한다")
  void existsByNickname_NonExistingNickname_ReturnsFalse() {
    boolean exists = userRepository.existsByNickname("없는닉네임");
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("ID로 삭제되지 않은 유저를 조회할 수 있다")
  void findByIdAndDeletedAtIsNull_ReturnsUser() {
    User user = createTestUser("idtest@email.com", "아이디조회");
    userRepository.save(user);
    em.flush();
    em.clear();

    Optional<User> found = userRepository.findByIdAndDeletedAtIsNull(user.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("idtest@email.com");
  }

  @Test
  @Transactional
  @DisplayName("soft delete 쿼리가 정상적으로 deletedAt을 설정한다")
  void deleteByIdForSoft_SetsDeletedAt() {
    User user = createTestUser("delete@email.com", "삭제유저");
    userRepository.save(user);
    em.flush();

    Instant now = Instant.now();
    userRepository.deleteByIdForSoft(user.getId(), now);
    em.flush();
    em.clear();

    User deletedUser = em.find(User.class, user.getId());
    assertThat(deletedUser.getDeletedAt()).isNotNull();
  }
}
