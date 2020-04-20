package de.maik.reactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

class FluxTest {

    List<String> names = Arrays.asList("jack", "judy", "james", "julia");

    @Test
    void subscribeToDifferentEventTypes() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Error reading Flux")))
                .concatWith(Flux.just("Element after Error"))
                .log();
        stringFlux.subscribe(
                System.out::println, // Call for onNext()
                System.err::println, // Call for onError
                () -> System.out.println("Flux completed")); // Call for onComplete() - won't happen here as an Error is emitted
    }

    @Test
    void readElementsFromFluxWithoutErrors() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        // Assert Flux conditions with StepVerifier
        // Must end with verifyComplete() to subscribe
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    void readElementsFromFluxWithError() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException()))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void readElementsCountWithErrorMessage() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("WTF!?")
                .verify();
    }

    @Test
    void canCreateFluxFromIterable() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("jack", "judy", "james", "julia")
                .verifyComplete();
    }

    @Test
    void canCreateFluxFromArray() {
        String[] names = new String[]{"jack", "judy", "james", "julia"};
        Flux<String> namesFlux = Flux
                .fromArray(names);
        StepVerifier.create(namesFlux)
                .expectNext("jack", "judy", "james", "julia")
                .verifyComplete();
    }

    @Test
    void canCreateFluxFromStream() {
        Flux<String> namesFlux = Flux
                .fromStream(names.stream());
        StepVerifier.create(namesFlux)
                .expectNext("jack", "judy", "james", "julia")
                .verifyComplete();
    }

    @Test
    void canCreateFluxFromRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    void canCreateFluxFromFilteredIterable() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .filter(name -> name.startsWith("ju"))
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("judy",  "julia")
                .verifyComplete();
    }

    @Test
    void canTransformFluxWithUppercaseOperation() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .map(String::toUpperCase)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("JACK", "JUDY", "JAMES", "JULIA")
                .verifyComplete();
    }

    @Test
    void canTransformFluxToDifferentTypeAndRepeatsOnce() {
        Flux<Integer> namesFlux = Flux
                .fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext(4,4,5,5,4,4,5,5)
                .verifyComplete();
    }

    @Test
    void canPerformActionForEveryElementUsingFlatMap() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A","B","C","D","E","F","G"))
                .flatMap(name -> {
                    return Flux.fromIterable(callExternalConverter(name));
                })
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(14)
                .verifyComplete();
    }

    @Test
    void canPerformActionForEveryElementUsingFlatMapUsingParallelScheduling() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A","B","C","D","E","F","G","H"))
                .window(2)// Returns Flux<Flux<String>> -> (A,B), (C,D), ..
                .flatMap(name ->
                    name
                            .map(this::callExternalConverter)
                            .subscribeOn(parallel()) // Perform external call for both elements, spawn parallel threads
                            .flatMap(Flux::fromIterable) // Convert result to Flux
                )
                .log(); // Order will most likely not be retained

        StepVerifier
                .create(stringFlux)
                .expectNextCount(16)
                .verifyComplete();
    }

    @Test
    void canPerformActionForEveryElementUsingFlatMapUsingParallelSchedulingOrdered() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A","B","C","D","E","F","G","H"))
                .window(2)
                .flatMapSequential(name -> // FlatMapSequential retains order
                        name
                                .map(this::callExternalConverter)
                                .subscribeOn(parallel())
                                .flatMap(Flux::fromIterable)
                )
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(16)
                .verifyComplete();
    }

    /**
     * Mimick external service, convert String to List
     * @param name
     * @return String s -> List[s, Z]
     */
    private List<String> callExternalConverter(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(name, "Z");
    }

}
