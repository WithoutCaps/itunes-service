package io.gytis.itunes.api.config;


import io.gytis.itunes.dtos.LookupDto;
import io.gytis.itunes.dtos.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public ReactiveRedisOperations<String, SearchDto> searchRedis(ReactiveRedisConnectionFactory factory) {
        var serializer = new Jackson2JsonRedisSerializer<>(SearchDto.class);
        var builder = RedisSerializationContext.<String, SearchDto>newSerializationContext(new StringRedisSerializer());
        var context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisOperations<String, LookupDto> lookupRedis(ReactiveRedisConnectionFactory factory) {
        var serializer = new Jackson2JsonRedisSerializer<>(LookupDto.class);
        var builder = RedisSerializationContext.<String, LookupDto>newSerializationContext(new StringRedisSerializer());
        var context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
