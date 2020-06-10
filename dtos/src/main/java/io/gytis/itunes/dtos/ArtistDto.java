package io.gytis.itunes.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * {
 * "wrapperType":"artist",
 * "artistType":"Artist",
 * "artistName":"ABBA",
 * "artistLinkUrl":"https://music.apple.com/us/artist/abba/372976?uo=4",
 * "artistId":372976,
 * "amgArtistId":3492,
 * "primaryGenreName":"Pop",
 * "primaryGenreId":14
 * }
 */
@Getter
@ToString
@EqualsAndHashCode
public class ArtistDto {
    private String artistLinkUrl;
    private String artistName;
    private String primaryGenreName;
    private int amgArtistId;
    private int artistId;
    private int primaryGenreId;
}
