package de.maik.reactivespring.demo.boundary.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basically the same test cases as standard rest controller tests as the behavior is
 * the same - the implementation is different however using the functional approach,
 * and we thus need a full fledged application context in order for the tests to work
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest // Will fire up the whole application context
@AutoConfigureWebTestClient
@DirtiesContext
class SampleHandlerTest {

    private static final String FLUX_ENDPOINT_URI = "/functional/flux";
    private static final String MONO_ENDPOINT_URI = "/functional/mono";
    @Autowired
    WebTestClient webTestClient;

    @Test
    void canCallAndConsumeFluxEndpointValuesAs1234() {
        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri(FLUX_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        // Evaluates concrete values
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    void canCallAndConsumeMono() {
        webTestClient.get().uri(MONO_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(1));
    }


}