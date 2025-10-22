package com.sprint.project.monew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoNewApplication {

  public static void main(String[] args) {
    SpringApplication.run(MoNewApplication.class, args);
    System.out.println("접속 -> http://localhost:8080/");
  }

}
