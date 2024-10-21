package org.mrpaulwoods.reactivecustomers.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrpaulwoods.reactivecustomers.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    private List<Customer> customers = List.of(
            new Customer("Malcolm", "Reynolds"),
            new Customer("Zoë", "Washburne"),
            new Customer("Hoban", "Washburne"),
            new Customer("Jayne", "Cobb"),
            new Customer("Kaylee", "Frye"));

    @BeforeEach
    void setup() {
        repository
                .deleteAll()
                .thenMany(Flux.fromIterable(customers))
                .flatMap(repository::save)
                .blockLast();
    }

    @Test
    void fetchAllCustomers() {
        repository.findAll()
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void fetchCustomerById() {
        repository.findById(customers.get(0).getId())
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.getFirstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void findByLastName() {
        repository.findByLastName("Reynolds")
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.getFirstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void insertCustomer() {
        Customer newCustomer = new Customer("Inara", "Serra");
        repository.save(newCustomer)
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.getFirstName().equals("Inara"))
                .verifyComplete();
    }

    @Test
    void updateCustomer() {
        Customer updatedCustomer = new Customer(
                customers.get(0).getId(),
                "Malcolm",
                "Reynolds, Jr.");

        repository.save(updatedCustomer)
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.getLastName().equals("Reynolds, Jr."))
                .verifyComplete();

        repository.findById(customers.get(0).getId())
                .log()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.getLastName().equals("Reynolds, Jr."))
                .verifyComplete();
    }

    @Test
    void deleteCustomer() {
        repository.deleteById(customers.get(0).getId())
                .log()
                .as(StepVerifier::create)
                .verifyComplete();

        repository.count()
                .doOnNext(System.out::println)
                .log()
                .as(StepVerifier::create)
                .expectNextMatches(count -> count == 4)
                .verifyComplete();
    }

}