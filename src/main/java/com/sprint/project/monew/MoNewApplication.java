package com.sprint.project.monew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class MoNewApplication {

  public static void main(String[] args) {
    SpringApplication.run(MoNewApplication.class, args);
    System.out.println("http://localhost:8080/");
    System.out.println("접속 -> http://localhost:8080/");
  }




}
