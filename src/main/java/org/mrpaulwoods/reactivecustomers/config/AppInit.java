package org.mrpaulwoods.reactivecustomers.config;

import org.mrpaulwoods.reactivecustomers.dao.CustomerRepository;
import org.mrpaulwoods.reactivecustomers.entities.Customer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AppInit {


    @Bean
    CommandLineRunner initializeDatabase(CustomerRepository repository) {
        return args ->
                repository.count().switchIfEmpty(Mono.just(0L))
                        .flatMapMany(count -> repository.deleteAll()
                                .thenMany(Flux.just(
                                        new Customer("Malcolm", "Reynolds"),
                                        new Customer("Zoë", "Washburne"),
                                        new Customer("Hoban", "Washburne"),
                                        new Customer("Jayne", "Cobb"),
                                        new Customer("Kaylee", "Frye")))
                                .flatMap(repository::save))
                        .subscribe(System.out::println);
    }

}
