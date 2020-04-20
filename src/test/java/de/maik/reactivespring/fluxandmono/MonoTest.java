package de.maik.reactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

class MonoTest {

    @Test
    void canSubscribeToAndEmitString() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier
                .create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    void canSubscribeToMonoWithError() {
        Mono<String> stringMono = Mono.error(new RuntimeException("Error!"));
        StepVerifier
                .create(stringMono.log())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void canCreateEmptyMono() {
        Mono<String> emptyMono = Mono.justOrEmpty(null);
        StepVerifier
                .create(emptyMono.log())
                .verifyComplete();
    }

    @Test
    void canCreateMonoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "adam";
        Mono<String> stringMono = Mono
                .fromSupplier(stringSupplier); // Will use get() method to read value
        StepVerifier
                .create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }
}
