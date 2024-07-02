package com.example.restapi;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v1/protos/customers")
public class CustomerProtobufRestController {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerProtobufRestController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<CustomerProtos.Customer> get(@PathVariable("id") Long id) throws CustomerNotFoundException {
        Optional<Customer> cu = this.customerRepository.findById(id);
        CustomerProtos.Customer sd = fromEntityToProtobuf(cu.get());
        System.out.print("직렬화 된 데이터 모습 : \n"+sd+"\n---------------------------------");
        return this.customerRepository.findById(id)
                .map(this::fromEntityToProtobuf)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping
    ResponseEntity<CustomerProtos.Customers> getCollection() {
//        Log consolelog = LogFactory.getLog(getClass());
        List<Customer> all = this.customerRepository.findAll();
//        all.forEach(consolelog::info);
        CustomerProtos.Customers customers = this.fromCollectionToProtobuf(all);

        return ResponseEntity.ok(customers);
    }

    @PostMapping
    ResponseEntity<CustomerProtos.Customer> post(@RequestBody CustomerProtos.Customer c) {
        Customer customer = this.customerRepository.save(new Customer(c.getFirstName(), c.getLastName()));

        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(customer.getId()).toUri();

        return ResponseEntity.created(uri)
                .body(this.fromEntityToProtobuf(customer));
    }

    @PutMapping("/{id}")
    ResponseEntity<CustomerProtos.Customer> put(@PathVariable Long id, @RequestBody CustomerProtos.Customer c) throws CustomerNotFoundException {
        return this.customerRepository.findById(id)
                .map(
                        existing -> {
                            Customer customer =
                                    this.customerRepository.save(
                                            new Customer(existing.getId(), c.getFirstName(), c.getLastName())
                                    );
                            URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                            return ResponseEntity.created(selfLink)
                                    .body(fromEntityToProtobuf(customer));
                        }
                ).orElseThrow(() -> new CustomerNotFoundException(id));
    }


    private CustomerProtos.Customer fromEntityToProtobuf(Customer c) {
        return fromEntityToProtobuf(c.getId(), c.getFirstName(), c.getLastName());
    }

    private CustomerProtos.Customers fromCollectionToProtobuf(Collection<Customer> c) {
        return CustomerProtos.Customers
                .newBuilder()
                .addAllCustomer(
                        c.stream()
                                .map(this::fromEntityToProtobuf)
                                .collect(Collectors.toList())
                ).build();
    }

    private CustomerProtos.Customer fromEntityToProtobuf(Long id, String f, String l) {
        CustomerProtos.Customer.Builder builder = CustomerProtos.Customer.newBuilder();
        if (id != null && id > 0) {
            builder.setId(id);
        }

        return builder.setFirstName(f).setLastName(l).build();
    }
}
