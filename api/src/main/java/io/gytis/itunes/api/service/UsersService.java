package io.gytis.itunes.api.service;

import io.gytis.itunes.api.repository.UsersRepository;
import io.gytis.itunes.dtos.UserDto;
import io.gytis.itunes.dtos.UserUpdateDto;
import io.gytis.itunes.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepo;

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getFavoriteAmgArtistsIds());
    }

    public Mono<UserDto> toDto(Mono<User> user) {
        return user.map(this::toDto);
    }

    public Mono<User> updateViaDto(int id, UserUpdateDto userDto) {
        return usersRepo.findById(id)
                .map(it -> {
                    it.setName(userDto.getName());
                    it.setFavoriteAmgArtistsIds(userDto.getFavoriteAmgArtistsIds());
                    return it;
                }).flatMap(usersRepo::save);
    }

    public User toEntity(UserUpdateDto user) {
        return new User()
                .setName(user.getName())
                .setFavoriteAmgArtistsIds(user.getFavoriteAmgArtistsIds());

    }
}
