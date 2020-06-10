package io.gytis.itunes.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gytis.itunes.dtos.AlbumDto;
import io.gytis.itunes.dtos.LookupDto;
import io.gytis.itunes.dtos.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItunesService {

    private final ReactiveRedisOperations<String, SearchDto> searchRedis;
    private final ReactiveRedisOperations<String, LookupDto> lookupRedis;
    private final ObjectMapper jackson;

    private final WebClient itunesClient = WebClient
            .builder()
            .baseUrl("https://itunes.apple.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(logFilter())
            .build();

    private ExchangeFilterFunction logFilter() {
        return (clientRequest, next) -> {
            log.info("Request: " + clientRequest.url().toString());
            return next.exchange(clientRequest);
        };
    }

    public Mono<SearchDto> search(final String term) {
        return this.searchRedis.opsForValue().get(term)
                .switchIfEmpty(
                        Mono.defer(() ->
                                doSearchRequest(term).map(result -> {
                                    this.searchRedis.opsForValue().set(term, result, Duration.ofDays(1)).subscribe();
                                    return result;
                                })
                        )
                );
    }

    public Mono<SearchDto> doSearchRequest(final String term) {
        return itunesClient.method(HttpMethod.GET)
                .uri("/search?entity=allArtist&term={term}", term)
                .retrieve()
                .bodyToMono(String.class)
                .map(it -> parseJson(it, SearchDto.class));
    }

    public Flux<AlbumDto> getTop5(final int amgArtistId) {
        var id = String.valueOf(amgArtistId);
        return this.lookupRedis.opsForValue().get(id)
                .switchIfEmpty(Mono.defer(() ->
                        doLookupRequest(List.of(amgArtistId)).map(it -> {
                            this.lookupRedis.opsForValue().set(id, it).subscribe();
                            return it;
                        })
                ))
                .flatMapIterable(LookupDto::getResults);
    }

    public Mono<LookupDto> doLookupRequest(final List<Integer> amgArtistIds) {
        var joinedIds = amgArtistIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return itunesClient.method(HttpMethod.GET)
                .uri("/lookup?amgArtistId={amgArtistId}&entity=album&limit=5", joinedIds)
                .retrieve()
                .bodyToMono(String.class)
                .map(it -> parseJson(it, LookupDto.class))
                .flatMapIterable(LookupDto::getResults)
                .filter(it -> it.getCollectionId() != 0)
                .collect(Collectors.toList())
                .map(LookupDto::new);
    }

    // Every midnight
    @Scheduled(cron = "0 0 * * *")
    public void refreshTop5AlbumsCache() {

        this.lookupRedis.keys("*")
                .map(Integer::parseInt)
                .collect(Collectors.toList())
                .flatMap(this::doLookupRequest)
                .flatMapIterable(LookupDto::getResults)
                .groupBy(AlbumDto::getAmgArtistId)
                .flatMap(it -> it.collect(Collectors.toList()))
                .map(LookupDto::new)
                .flatMap(it -> {
                    var amgArtistId = String.valueOf(it.getResults().get(0).getAmgArtistId());
                    return this.lookupRedis.opsForValue().set(amgArtistId, it);
                })
                .collect(Collectors.toList())
                .block();
    }

    @SneakyThrows
    private <T> T parseJson(final String json, final Class<T> clazz) {
        return jackson.readValue(json, clazz);
    }

}
