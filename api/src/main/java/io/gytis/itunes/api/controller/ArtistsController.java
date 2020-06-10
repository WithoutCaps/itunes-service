package io.gytis.itunes.api.controller;

import io.gytis.itunes.dtos.AlbumDto;
import io.gytis.itunes.dtos.LookupDto;
import io.gytis.itunes.dtos.SearchDto;
import io.gytis.itunes.api.service.ItunesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@SuppressWarnings("unused")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/artists")
public class ArtistsController {
    private final ItunesService itunesService;

    @GetMapping
    private Mono<SearchDto> get(@RequestParam String term) {
        return itunesService.search(term);
    }

    @GetMapping("/{id}/top")
    private Flux<AlbumDto> getArtist(@PathVariable("id") int id) {
        return itunesService.getTop5(id);
    }

}
