package org.mrpaulwoods.reactivecustomers.controllers;

import org.mrpaulwoods.reactivecustomers.dao.CustomerRepository;
import org.mrpaulwoods.reactivecustomers.entities.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
class CustomerController {

    private final CustomerRepository repository;

    CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    Flux<Customer> findAll() {
        return repository.findAll().log();
    }

    @GetMapping("{id}")
    Mono<Customer> findCustomer(@PathVariable("id") Long id) {
        return repository.findById(id).switchIfEmpty(
                Mono.error(new IllegalArgumentException("Customer with id %d not found".formatted(id)))
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Customer> create(@RequestBody Customer customer) {
        return repository.save(customer);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Customer with id %d not found".formatted(id))))
                .flatMap(repository::delete);
    }

}
