package com.example.restapi;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@Component
public class CustomerResourceAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {

    @Override
    public EntityModel<Customer> toModel(Customer customer) {
        Customer c = new Customer();
        EntityModel<Customer> customerEntity =  EntityModel.of(c);
        URI photoUri = null;
        URI selfUri = null;
        try {
            photoUri = MvcUriComponentsBuilder
                    .fromMethodCall(
                            MvcUriComponentsBuilder
                                    .on(CustomerProfilePhotoRestController.class)
                                    .read(customer.getId())
                    )
                    .buildAndExpand().toUri();
             selfUri = MvcUriComponentsBuilder
                    .fromMethodCall(
                            MvcUriComponentsBuilder
                                    .on(CustomerProtobufRestController.class)
                                    .get(customer.getId())
                    )
                    .buildAndExpand().toUri();
        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }
        customerEntity.add( Link.of(selfUri.toString(), "self"));
        customerEntity.add(Link.of(photoUri.toString(), "profile-photo"));
        return customerEntity;
    }
}
