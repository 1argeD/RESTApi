package com.example.restapi;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping(value = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {
    private final CustomerResourceAssembler customerResourceAssembler;
    private final CustomerRepository customerRepository;

    CustomerHypermediaRestController(CustomerResourceAssembler cra, CustomerRepository repository) {
        this.customerRepository = repository;
        this.customerResourceAssembler = cra;
    }

    @GetMapping
    ResponseEntity<CollectionModel<Object>>root() {
        CollectionModel<Object> obj = CollectionModel.of(Collections.emptyList());
        URI uri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(getClass()).getCollection()
                ).build().toUri();
        Link link = Link.of(uri.toString(), "customer");
        obj.add(link);
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/customers")
    ResponseEntity<CollectionModel<EntityModel<Customer>>> getCollection() {
        List<EntityModel<Customer>> collect = this.customerRepository.findAll().stream()
                .map(customerResourceAssembler::toModel).toList();

        CollectionModel<EntityModel<Customer>> entityModel= CollectionModel.of(collect);
        URI self = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toUri();
        entityModel.add(Link.of(self.toString(), "self"));
        return ResponseEntity.ok(entityModel);
    }

    @RequestMapping(value = "/customers", method = RequestMethod.OPTIONS)
    ResponseEntity<?> option() {
        return ResponseEntity.ok()
                .allow(HttpMethod.HEAD,HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.GET,HttpMethod.OPTIONS)
                .build();
    }

    @GetMapping(value = "/customers/{id}")
    ResponseEntity<EntityModel<Customer>> get (@PathVariable Long id) throws CustomerNotFoundException{
        return this.customerRepository.findById(id)
                .map( c-> ResponseEntity.ok(
                        this.customerResourceAssembler.toModel(c)
                )).orElseThrow(()-> new CustomerNotFoundException(id));
    }

    @PostMapping(value = "/customers")
    ResponseEntity<EntityModel<Customer>> post(@RequestBody Customer c) {
        Customer customer = this.customerRepository.save(
                new Customer(c.getFirstName(), c.getLastName()));
                URI uri = MvcUriComponentsBuilder.fromController(getClass())
                        .path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(uri)
                .body(this.customerResourceAssembler.toModel(customer));
    }

    @DeleteMapping(value = "/customers/{id}")
    ResponseEntity<?> delete(@PathVariable Long id)  throws  CustomerNotFoundException{
        return this.customerRepository.findById(id)
                .map(c -> {
                    customerRepository.delete(c);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(()-> new CustomerNotFoundException(id));
    }

    @RequestMapping(value="/customers/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) throws  CustomerNotFoundException{
        return this.customerRepository.findById(id)
                .map(exist ->
                    ResponseEntity.noContent().build()
                ).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<EntityModel<Customer>> put(@PathVariable Long id, @RequestBody Customer customer) {
        Customer c = this.customerRepository.save(
                    new Customer(id, customer.getFirstName(), customer.getFirstName())
        );
        EntityModel<Customer> entityModel = this.customerResourceAssembler.toModel(c);
        URI selfLink = URI.create(
                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString()
        ) ;

        return ResponseEntity.created(selfLink).body(entityModel);
    }
}
