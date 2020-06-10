package io.gytis.itunes.integration;


import io.gytis.itunes.api.Application;
import io.gytis.itunes.api.service.ItunesService;
import io.gytis.itunes.dtos.AlbumDto;
import io.gytis.itunes.dtos.SearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistsControllerTest {

    private Logger log = LoggerFactory.getLogger(ArtistsControllerTest.class);
    @Autowired
    private ItunesService itunesService;
    @Autowired
    private RedisConnectionFactory factory;

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
        factory.getConnection().flushDb();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abba"})
    void search(String term) {
        Stream.of(1, 2).forEach(loop -> {
            log.info("######## LOOP: " + loop);
            var result = client.get()
                    .uri("/api/artists?term=" + term)
                    .retrieve().bodyToMono(SearchDto.class)
                    .flatMapIterable(SearchDto::getResults)
                    .collect(Collectors.toList())
                    .block();

            assertThat(result).isNotEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {3492})
    void lookup(int id) {
        Stream.of(1, 2).forEach(loop -> {
            log.info("######## LOOP: " + loop);
            var result = client.get()
                    .uri("/api/artists/" + id + "/top")
                    .retrieve().bodyToFlux(AlbumDto.class)
                    .collect(Collectors.toList())
                    .block();

            result.forEach(it -> log.info(it.toString()));

            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(5);
        });
    }

    @Test
    void refreshCache() {
        itunesService.refreshTop5AlbumsCache();
    }
}
