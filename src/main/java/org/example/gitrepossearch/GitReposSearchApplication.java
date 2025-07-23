package org.example.gitrepossearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableCaching
public class GitReposSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitReposSearchApplication.class, args);
    }

    @Bean
    public Executor githubExecutor() {
        return Executors.newFixedThreadPool(10);
    }

}
