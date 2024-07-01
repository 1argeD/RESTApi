package com.example.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping(value = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {
    private final CustomerResourceAssembler customerResourceAssembler;
    private final CustomerRepository customerRepository;

    CustomerHypermediaRestController(CustomerResourceAssembler cra, CustomerRepository repository) {
        this.customerRepository = repository;
        this.customerResourceAssembler = cra;
    }

//    @GetMapping
//    ResponseEntity<CollectionModel<Object>>root() {
//        CollectionModel<Object> obj = CollectionModel.of(Collections.emptyList());
//        URI uri = MvcUriComponentsBuilder
//                .fromMethodCall(
//                        MvcUriComponentsBuilder.on(getClass()).getCollection()
//                )
//    }

}
