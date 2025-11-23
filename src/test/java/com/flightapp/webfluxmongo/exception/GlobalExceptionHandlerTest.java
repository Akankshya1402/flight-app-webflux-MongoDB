package com.flightapp.webfluxmongo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = DummyExceptionController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testNotFoundException() {
        webTestClient.get()
                .uri("/test/throw-notfound")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Flight not found")
                .jsonPath("$.path").isEqualTo("/test/throw-notfound");
    }

    @Test
    void testBadRequestException() {
        webTestClient.get()
                .uri("/test/throw-badrequest")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("Invalid input")
                .jsonPath("$.path").isEqualTo("/test/throw-badrequest");
    }

    @Test
    void testGenericException() {
        webTestClient.get()
                .uri("/test/throw-server")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("Some server failure")
                .jsonPath("$.path").isEqualTo("/test/throw-server");
    }
}

