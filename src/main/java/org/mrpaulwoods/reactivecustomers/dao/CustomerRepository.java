package org.mrpaulwoods.reactivecustomers.dao;

import org.mrpaulwoods.reactivecustomers.entities.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Flux<Customer> findByLastName(String lastName);
}
