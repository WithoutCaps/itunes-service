package io.gytis.itunes.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Table("users")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public User(String name, List<Integer> favoriteAmgArtistsIds) {
        this.name = name;
        setFavoriteAmgArtistsIds(favoriteAmgArtistsIds);
    }

    @Id
    private Long id;
    private String name;
    private String favoriteAmgArtistsIds; // There seems to be some sort of issue with r2dbc and arrays as column type

    public List<Integer> getFavoriteAmgArtistsIds() {
        return Arrays.stream(favoriteAmgArtistsIds.split(","))
                .filter(it -> !it.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public User setFavoriteAmgArtistsIds(List<Integer> favoriteAmgArtistsIds) {
        var sj = new StringJoiner(",");
        favoriteAmgArtistsIds.forEach(x -> sj.add(String.valueOf(x)));
        this.favoriteAmgArtistsIds = sj.toString();
        return this;
    }
}
