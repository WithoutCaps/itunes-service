package io.gytis.itunes.integration;


import io.gytis.itunes.api.Application;
import io.gytis.itunes.api.repository.UsersRepository;
import io.gytis.itunes.api.service.UsersService;
import io.gytis.itunes.dtos.UserDto;
import io.gytis.itunes.dtos.UserUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.gytis.itunes.Utils.assertHttpStatus;
import static io.gytis.itunes.Utils.assertNotFound;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {

    @Autowired
    private UsersRepository usersRepo;
    @Autowired
    private UsersService usersService;

    private final List<UserDto> defaultUsers = Stream.of(1, 2, 3, 4, 5)
            .map(it -> new UserDto((long) it, "User " + it, Arrays.asList(1, 2, 3)))
            .collect(Collectors.toList());

    @LocalServerPort
    private int port;

    private WebClient client;

    @BeforeEach
    public void setup() {
        client = WebClient
                .builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Test
    void getAll() {
        var result = client.get()
                .uri("/api/users")
                .retrieve().bodyToFlux(UserDto.class)
                .collect(Collectors.toList())
                .block();

        assertThat(result).isEqualTo(defaultUsers);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void getUser(int userId) {
        var result = client.get()
                .uri("/api/users/" + userId)
                .retrieve().bodyToMono(UserDto.class)
                .block();

        assertThat(result).isEqualTo(defaultUsers.get(userId - 1));
    }

    @Test
    void getUserNegative() {
        var result = client.get().uri("/api/users/0");
        assertNotFound(result);
    }

    @Test
    @DirtiesContext
    void updateUser() {
        var userUpdate = new UserUpdateDto("new name", List.of(99, 100));
        var expectedResult = new UserDto(1L, "new name", List.of(99, 100));

        var result = client.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userUpdate))
                .retrieve().bodyToMono(UserDto.class)
                .block();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DirtiesContext
    void updateUserNegative() {
        var user = new UserUpdateDto("new name", List.of(99, 100));
        var result = client.put()
                .uri("/api/users/0")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user));
        assertNotFound(result);
    }

    @Test
    @DirtiesContext
    void createUser() {
        var userUpdate = new UserUpdateDto("new name", List.of(99, 100));
        var expectedResult = new UserDto(6L, "new name", List.of(99, 100));

        var result = client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userUpdate))
                .retrieve().bodyToMono(UserDto.class)
                .block();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DirtiesContext
    void deleteUser() {
        var result = client.delete().uri("/api/users/5");
        assertHttpStatus(result, HttpStatus.OK);
    }

    @Test
    void deleteUserNegative() {
        var result = client.delete().uri("/api/users/0");
        assertNotFound(result);
    }

}
