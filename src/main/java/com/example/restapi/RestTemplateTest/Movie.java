package com.example.restapi.RestTemplateTest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
public class Movie {
    public String title;
    @Id
    @GeneratedValue
    private Long id;

    Movie() {}
    Movie(String title) {
        this.title = title;
    }
}
