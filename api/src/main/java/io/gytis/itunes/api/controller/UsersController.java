package io.gytis.itunes.api.controller;

import io.gytis.itunes.api.repository.UsersRepository;
import io.gytis.itunes.dtos.UserDto;
import io.gytis.itunes.dtos.UserUpdateDto;
import io.gytis.itunes.api.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@SuppressWarnings("unused")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final ResponseStatusException USER_NOT_FOUND = new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    private final UsersRepository usersRepo;
    private final UsersService usersService;

    @GetMapping
    private Flux<UserDto> get() {
        return usersRepo.findAll()
                .map(usersService::toDto);
    }

    @GetMapping("/{id}")
    private Mono<UserDto> getUser(@PathVariable("id") int id) {
        return usersRepo.findById(id)
                .map(usersService::toDto)
                .switchIfEmpty(Mono.error(USER_NOT_FOUND));
    }

    @PostMapping
    private Mono<UserDto> createUser(@RequestBody UserUpdateDto body) {
        var user = usersService.toEntity(body);
        return usersService.toDto(this.usersRepo.save(user));
    }

    @PutMapping("/{id}")
    private Mono<UserDto> updateUser(@PathVariable("id") int id, @RequestBody UserUpdateDto body) {
        return usersService.updateViaDto(id, body)
                .map(usersService::toDto)
                .switchIfEmpty(Mono.error(USER_NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    private Mono<Void> deleteUser(@PathVariable("id") int id) {
        return this.usersRepo.findById(id)
                .switchIfEmpty(Mono.error(USER_NOT_FOUND))
                .flatMap(this.usersRepo::delete);
    }

}
