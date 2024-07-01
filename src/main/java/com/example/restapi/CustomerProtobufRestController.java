package com.example.restapi;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/protos/customers")
public class CustomerProtobufRestController {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerProtobufRestController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

//    @GetMapping(value = "/{id}")
//    ResponseEntity<Customerprotos>
}
