package com.example.restapi.RestTemplateTest;

import com.example.restapi.ResTapiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

public class RestTemplateTest {
    private Log log = LogFactory.getLog(getClass());
    private URI baseURL;
    private ConfigurableApplicationContext server;
    private RestTemplate restTemplate;
    private MovieRepository repository;
    private URI movieURI;

    @Before
    public void setUP() {
        this.server = new SpringApplicationBuilder()
                .properties(Collections.singletonMap("server.port","0"))
                .sources(ResTapiApplication.class).run();

        int port = this.server.getEnvironment()
                .getProperty("local.server.port",Integer.class, 8000);

        this.restTemplate = this.server.getBean(RestTemplate.class);
        this.baseURL = URI.create("http://localhost:" + port + "/");
        this.movieURI=URI.create(this.baseURL.toString() + "movie");
        this.repository = this.server.getBean(MovieRepository.class);
    }

    @After
    public void tearDown() {
        if(null != this.server) {
            this.server.close();
        }
    }

    @Test
    public void testRestTemplate() {
        ResponseEntity<Movie> postMovieRepositoryEntity = this.restTemplate
                .postForEntity(movieURI, new Movie("Forest Gump"), Movie.class);
        URI uriOfNewMovie = postMovieRepositoryEntity.getHeaders().getLocation();
        log.info("the new movie lives at " + uriOfNewMovie);

        JsonNode mapForMovieRecord = this.restTemplate
                .getForObject(Objects.requireNonNull(uriOfNewMovie), JsonNode.class);
        log.info("\t..read as a Map.class : " + mapForMovieRecord);

        Movie movieReference = this.restTemplate
                .getForObject(uriOfNewMovie, Movie.class);

        assertEquals(Objects.requireNonNull(mapForMovieRecord).get("title").asText(),
                Objects.requireNonNull(postMovieRepositoryEntity.getBody()).title);

        log.info("\t..read as a Movie.class : " + movieReference);

        ResponseEntity<Movie> movieResponseEntity = this.restTemplate
                .getForEntity(uriOfNewMovie, Movie.class);

        assertEquals(movieResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(movieResponseEntity.getHeaders().getContentType(),
                MediaType.parseMediaType("application/json;charset=UTF-8"));

        log.info("\t..read as a ResponseEntity<Movie> : " + movieResponseEntity);

        //@formatter:off
        ParameterizedTypeReference<CollectionModel<Movie>> movies = new ParameterizedTypeReference<CollectionModel<Movie>>() {};
        //@formatter:on

        ResponseEntity<CollectionModel<Movie>> moviesResponseEntity = this.restTemplate
                .exchange(this.movieURI, HttpMethod.GET, null, movies);
        CollectionModel<Movie> movieModels = moviesResponseEntity.getBody();
        Objects.requireNonNull(movieModels).forEach(this.log::info);

        assertEquals(movieModels.getContent().size(), this.repository.count());
        assertEquals(1, movieModels.getLinks().stream()
                .filter(m -> m.getRel().equals("self")).count());

    }

}

