package com.sprint.project.monew.respositroy;


import com.sprint.project.monew.notification.entity.NotificationEntity;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class NotificationRepositoryTest {

//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @Autowired
//    private TestEntityManager em;

//    @Test
//    @DisplayName("findAll Test")
//    void findAllTest() {
//
//        List<NotificationEntity> list  = notificationRepository.findAll();
//
//
//        assert !list.isEmpty();
//        System.out.println(list);
//
//
//
//
//    }


}
