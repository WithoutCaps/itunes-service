package io.gytis.itunes.api.service;

import io.gytis.itunes.api.entity.User;
import io.gytis.itunes.api.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class DataInitializer {

    private final DatabaseClient databaseClient;
    private final UsersRepository usersRepository;

    @EventListener(value = ContextRefreshedEvent.class)
    public void setup() {

        this.databaseClient
                .execute("DROP TABLE IF EXISTS USERS")
                .then().subscribe();

        this.databaseClient
                .execute("CREATE TABLE IF NOT EXISTS USERS (id INT AUTO_INCREMENT NOT NULL, name VARCHAR2 NOT NULL, FAVORITE_AMG_ARTISTS_IDS VARCHAR2 NOT NULL);")
                .then().subscribe();

        Stream.of(1, 2, 3, 4, 5).forEach(it ->
                usersRepository.save(new User("User " + it, Arrays.asList(1, 2, 3))).subscribe()
        );
    }

}