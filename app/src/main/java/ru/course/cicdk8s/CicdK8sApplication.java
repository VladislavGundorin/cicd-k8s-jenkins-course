package ru.course.cicdk8s;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CicdK8sApplication {

    private final String version = System.getenv().getOrDefault("APP_VERSION", "dev");

    public static void main(String[] args) {
        SpringApplication.run(CicdK8sApplication.class, args);
    }

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "message", "Hello from Jenkins CI/CD Kubernetes demo",
                "version", version
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("version", version);
    }
}
