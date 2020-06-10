package io.gytis.itunes.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
{
   "wrapperType":"collection",
   "collectionType":"Album",
   "artistId":372976,
   "collectionId":1422648512,
   "amgArtistId":3492,
   "artistName":"ABBA",
   "collectionName":"Gold: Greatest Hits",
   "collectionCensoredName":"Gold: Greatest Hits",
   "artistViewUrl":"https://music.apple.com/us/artist/abba/372976?uo=4",
   "collectionViewUrl":"https://music.apple.com/us/album/gold-greatest-hits/1422648512?uo=4",
   "artworkUrl60":"https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/88/92/4c/88924c01-6fb3-8616-f0b3-881b1ed09e03/source/60x60bb.jpg",
   "artworkUrl100":"https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/88/92/4c/88924c01-6fb3-8616-f0b3-881b1ed09e03/source/100x100bb.jpg",
   "collectionPrice":7.99,
   "collectionExplicitness":"notExplicit",
   "trackCount":19,
   "copyright":"This Compilation ℗ 2014 Polar Music International AB",
   "country":"USA",
   "currency":"USD",
   "releaseDate":"1992-09-21T07:00:00Z",
   "primaryGenreName":"Pop"
}
*/
@Getter
@ToString
@EqualsAndHashCode
public class AlbumDto {
    private String artistName;
    private String artistViewUrl;
    private String artworkUrl100;
    private String artworkUrl60;
    private String collectionCensoredName;
    private String collectionExplicitness;
    private String collectionName;
    private String collectionViewUrl;
    private String copyright;
    private String country;
    private String currency;
    private String primaryGenreName;
    private String releaseDate;
    private float collectionPrice;
    private int amgArtistId;
    private int artistId;
    private int collectionId;
    private int trackCount;
}
