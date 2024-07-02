package com.example.restapi;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/v1/customer")
@RequiredArgsConstructor
public class CustomerRestController {
    private final CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> Entity() {
        return ResponseEntity
                .ok()
                .allow(
                        HttpMethod.GET, HttpMethod.DELETE
                        , HttpMethod.HEAD, HttpMethod.OPTIONS
                        , HttpMethod.PUT, HttpMethod.POST
                )
                .build();
    }

    @GetMapping
    public ResponseEntity<Collection<Customer>> getCollections() {
        return ResponseEntity.ok(this.customerRepository.findAll());
    }


    @GetMapping(value="/{id}")
    public ResponseEntity<Customer> get(@PathVariable Long id)  throws CustomerNotFoundException {
        return this.customerRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(()-> new CustomerNotFoundException(id));
    }
    @PostMapping
    ResponseEntity<Customer> post(@RequestBody Customer c) {
        Customer customer = this.customerRepository.save(new Customer(c.getFirstName(), c.getLastName()));
        URI uri = MvcUriComponentsBuilder
                .fromController(getClass()).path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return  ResponseEntity.created(uri).body(customer);
    }

    @DeleteMapping(value="/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) throws CustomerNotFoundException  {
        return this.customerRepository.findById(id)
                .map(c->{
                    customerRepository.delete(c);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(()-> new CustomerNotFoundException(id));
    }

    @RequestMapping(value="/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?>  head(@PathVariable("id")  Long id) throws CustomerNotFoundException {
        return this.customerRepository.findById(id)
                .map(exist-> ResponseEntity.noContent().build())
                .orElseThrow(()-> new CustomerNotFoundException(id));
    }

    @PutMapping(value = "/{id}")
    ResponseEntity<Customer> put(@PathVariable("id") Long id,
                                 @RequestBody Customer c) throws CustomerNotFoundException {
        return this.customerRepository
                .findById(id)
                .map(
                        existing -> {
                            Customer customer = this.customerRepository
                                    .save(new Customer(existing.getId(), c.getFirstName(), c.getLastName()));
                            URI selfLink = URI.create(
                                    ServletUriComponentsBuilder
                                            .fromCurrentRequest()
                                            .toUriString());
                            return ResponseEntity.created(selfLink).body(customer);
                        }
                )
                .orElseThrow(()-> new CustomerNotFoundException(id));
    }

}

