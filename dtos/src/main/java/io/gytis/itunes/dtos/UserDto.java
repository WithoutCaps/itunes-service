package io.gytis.itunes.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private List<Integer> favoriteAmgArtistsIds;
}
