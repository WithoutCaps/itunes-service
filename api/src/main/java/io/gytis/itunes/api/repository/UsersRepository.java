package io.gytis.itunes.api.repository;


import io.gytis.itunes.api.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UsersRepository extends ReactiveCrudRepository<User, Integer> {
}

