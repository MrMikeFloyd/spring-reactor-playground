package de.maik.reactivespring.demo.boundary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest // Will only scan for rest controllers and web components
@ExtendWith(SpringExtension.class)
@DirtiesContext
class FluxAndMonoControllerTest {

    private static final String FLUX_ENDPOINT_URI = "/flux";
    private static final String FLUX_STREAM_ENDPOINT_URI = "/fluxstream";
    private static final String MONO_ENDPOINT_URI = "/mono";

    @Autowired
    WebTestClient webTestClient; // equivalent to TestRestTemplate from SpringMVC

    @Test
    void canCallAndConsumeFluxEndpointValuesAs1234() {
        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri(FLUX_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
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
    void canCallAndConsumeFluxEndpointAsListOf4IntegerValues() {
        webTestClient
                .get()
                .uri(FLUX_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class).hasSize(4); // We don't care about the actual values, 4 Ints is enough here
    }

    @Test
    void canCallAndConsumeFluxEndpointValuesAsListOfIntegers() {
        List expectedResultList = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> result = webTestClient
                .get()
                .uri(FLUX_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        // Compare as lists
        assertThat(result.getResponseBody()).isEqualTo(expectedResultList);
    }

    @Test
    void canCallAndConsumeFluxEndpointValuesUsingConsumer() {
        List expectedResultList = Arrays.asList(1, 2, 3, 4);

        webTestClient
                .get()
                .uri(FLUX_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(expectedResultList));
    }

    @Test
    void canCallAndConsumeFirst4ElementsFromInfiniteFluxStream() {
        Flux<Long> longFluxStream = webTestClient
                .get()
                .uri(FLUX_STREAM_ENDPOINT_URI)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFluxStream)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .thenCancel()
                .verify();
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