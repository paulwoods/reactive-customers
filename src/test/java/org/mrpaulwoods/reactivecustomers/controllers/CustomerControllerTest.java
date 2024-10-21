package org.mrpaulwoods.reactivecustomers.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrpaulwoods.reactivecustomers.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        var statements = Arrays.stream("""
                        DROP TABLE IF EXISTS customer;
                        CREATE TABLE customer (
                        id long generated always as identity primary key,
                        first_name varchar(100) not null,
                        last_name varchar(100) not null
                        );
                        insert into customer (first_name, last_name) values ('Malcolm', 'Reynolds');
                        insert into customer (first_name, last_name) values ('Zoë', 'Washburne');
                        insert into customer (first_name, last_name) values ('Hoban', 'Washburne');
                        insert into customer (first_name, last_name) values ('Jayne', 'Cobb');
                        insert into customer (first_name, last_name) values ('Kaylee', 'Frye');
                        """
                        .split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        statements.forEach(it -> databaseClient
                .sql(it)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete());

    }

    private List<Long> getIds() {
        return databaseClient.sql("select id from customer")
                .map(row -> row.get("id", Long.class))
                .all()
                .collectList()
                .block();
    }

    @Test
    void findAll() {
        client.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .hasSize(5);
    }

    @Test
    void findById() {
        getIds().forEach(id ->
                client.get()
                        .uri("/customers/%d".formatted(id))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Customer.class)
                        .value(customer -> assertEquals(id, customer.getId())));
    }
    @Test
    void create() {
        Customer customer = new Customer(null, "Inara", "Serra");
        client.post()
                .uri("/customers")
                .bodyValue(customer)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class)
                .value(c -> assertEquals("Inara", c.getFirstName()));
    }

    @Test
    void delete() {
        getIds().forEach(id ->
                client.delete()
                        .uri("/customers/%d".formatted(id))
                        .exchange()
                        .expectStatus().isNoContent());
    }

    @Test
    void deleteNotFound() {
        client.delete()
                .uri("/customers/999")
                .exchange()
                .expectStatus().isNotFound();
    }

}