package de.maik.reactivespring.demo.boundary.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandler {

    /**
     * Handler Method that sends a Flux of 4 digits
     * enclosed in a Mono of ServerResponse
     */
    public Mono<ServerResponse> flux(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(1, 2, 3, 4).log(), Integer.class);
    }

    /**
     * Handler Method that sends a Mono of a digit
     * enclosed in a Mono of ServerResponse
     */
    public Mono<ServerResponse> mono(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(1).log(), Integer.class);
    }
}
