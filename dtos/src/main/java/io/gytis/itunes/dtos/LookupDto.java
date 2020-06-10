package io.gytis.itunes.dtos;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class LookupDto {
    private List<AlbumDto> results;
}
