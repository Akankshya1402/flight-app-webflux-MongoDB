package com.flightapp.webfluxmongo.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DummyExceptionController {

    @GetMapping("/test/throw-notfound")
    public Mono<String> throwNotFound() {
        return Mono.error(new ResourceNotFoundException("Flight not found"));
    }

    @GetMapping("/test/throw-badrequest")
    public Mono<String> throwBadRequest() {
        return Mono.error(new IllegalArgumentException("Invalid input"));
    }

    @GetMapping("/test/throw-server")
    public Mono<String> throwServerError() {
        return Mono.error(new RuntimeException("Some server failure"));
    }
}

