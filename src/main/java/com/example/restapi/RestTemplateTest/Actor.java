package com.example.restapi.RestTemplateTest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Actor {
    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    Actor(String  fullName) {
        this.fullName = fullName;
    }
    Actor() {

    }

}
