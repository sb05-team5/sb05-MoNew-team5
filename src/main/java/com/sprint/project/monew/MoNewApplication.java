package com.sprint.project.monew;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class MoNewApplication {

  public static void main(String[] args) {
    SpringApplication.run(MoNewApplication.class, args);
    System.out.println("http://localhost:8080/");
  }
}
