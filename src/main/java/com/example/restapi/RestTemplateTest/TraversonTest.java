package com.example.restapi.RestTemplateTest;


import com.example.restapi.ResTapiApplication;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.client.Traverson;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;

public class TraversonTest {
    private Log log  = LogFactory.getLog(getClass());
    private ConfigurableApplicationContext server;
    private Traverson traverson;

    @Before
    public void setUP() {
        this.server = new SpringApplicationBuilder()
                .properties(Collections.singletonMap("server.port", 0))
                .sources(ResTapiApplication.class).run();
        this.traverson = this.server.getBean(Traverson.class);
    }

    @After
    public void tearDowm() {
        if(null != this.server) {
            this.server.close();
        }
    }

    @Test
    public void testTraverson() {
        String nameOfMovice = "Cars";
        CollectionModel<Actor> actorEntityModel = this.traverson
                .follow("actor","search","by-movie")
                .withTemplateParameters(Collections.singletonMap("movie", nameOfMovice))
                .toObject(new ParameterizedTypeReference<CollectionModel<Actor>>(){});

        Objects.requireNonNull(actorEntityModel).forEach(this.log::info);
        assertEquals(
                actorEntityModel.getContent().stream()
                        .filter(actor -> actor.getFullName().equals("Owen Wilson")).toList().size(), 1);
    }
}
