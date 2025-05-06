package ua.com.edada.culinarynotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CulinaryNotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CulinaryNotesApplication.class, args);
    }
}