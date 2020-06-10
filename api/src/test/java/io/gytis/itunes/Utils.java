package io.gytis.itunes;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class Utils {
    private Utils() {
    }


    public static void assertNotFound(WebClient.RequestHeadersSpec<? extends WebClient.RequestHeadersSpec<?>> http) {
        assertHttpStatus(http, HttpStatus.NOT_FOUND);
    }

    public static void assertHttpStatus(WebClient.RequestHeadersSpec<? extends WebClient.RequestHeadersSpec<?>> http, HttpStatus status) {
        var result = http.exchange()
                .map(ClientResponse::statusCode)
                .block();

        assertThat(result).isEqualTo(status);
    }

}
