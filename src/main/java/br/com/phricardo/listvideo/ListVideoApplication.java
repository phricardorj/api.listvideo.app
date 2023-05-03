package br.com.phricardo.listvideo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ListVideoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ListVideoApplication.class, args);
  }
}
