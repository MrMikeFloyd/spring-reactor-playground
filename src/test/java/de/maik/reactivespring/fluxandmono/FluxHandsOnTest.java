package de.maik.reactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

class FluxHandsOnTest {

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
    void resumesOnErrorAndEmitsDefaultFluxData() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .concatWith(Flux.just("Spring Reactor"))
                .onErrorResume((e) -> {
                    System.out.println("An error occurred: " + e.getCause());
                    return Flux.just("Default Data 1", "Default Data 2");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring", "Default Data 1", "Default Data 2")
                .verifyComplete();
    }

    @Test
    void resumesOnErrorAndReturnsDefaultValue() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .concatWith(Flux.just("Spring Reactor"))
                .onErrorReturn("default")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring", "default")
                .verifyComplete();
    }

    @Test
    void allowsForCustomizedExceptionUsingMap() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .concatWith(Flux.just("Spring Reactor"))
                .onErrorMap(CustomException::new)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void allowsForRetryOnException() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .concatWith(Flux.just("Spring Reactor"))
                .onErrorMap(CustomException::new)
                .retry(2)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void allowsForRetryOnExceptionWithBackoffPeriod() {
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("WTF!?")))
                .concatWith(Flux.just("Spring Reactor"))
                .onErrorMap(CustomException::new)
                .retryBackoff(2, Duration.ofSeconds(5))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectError(IllegalStateException.class)// With exhaused retries will yield IllegalState
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
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    void canCreateFluxFromFilteredIterable() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .filter(name -> name.startsWith("ju"))
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("judy", "julia")
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
                .expectNext(4, 4, 5, 5, 4, 4, 5, 5)
                .verifyComplete();
    }

    @Test
    void canPerformActionForEveryElementUsingFlatMap() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F", "G"))
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
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"))
                .window(2) // Returns Flux<Flux<String>> -> (A,B), (C,D), ..
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
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"))
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

    @Test
    void canCombineFluxesUsingMergeButWontBeInOrder() {
        Flux<String> fluxA = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        Flux<String> fluxB = Flux.just("E", "F", "G", "H").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(fluxA, fluxB); // Won't wait for completion of fluxA - so won't be in order
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(8)
                //.expectNext("A","B","C","D","E","F","G","H") // this would fail
                .verifyComplete();
    }

    @Test
    void canCombineFluxesInOrderUsingConcat() {
        Flux<String> fluxA = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        Flux<String> fluxB = Flux.just("E", "F", "G", "H").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(fluxA, fluxB);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F", "G", "H") // Waits for completion of 1st flux => will be in order
                .verifyComplete();
    }

    @Test
    void canCombineFluxesIntoFluxContainingCombinedElements() {
        Flux<String> fluxA = Flux.just("A", "B", "C", "D");
        Flux<String> fluxB = Flux.just("E", "F", "G", "H");

        Flux<String> mergedFlux = Flux.zip(fluxA, fluxB, (a1, b1) -> {
            return a1.concat(b1); // Concat on Element level, emit new Element ab' with a + b
        });

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("AE", "BF", "CG", "DH")
                .verifyComplete();
    }

    @Test
    void canEmitInfiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux
                .interval(Duration.ofMillis(100)) // will emit element every 100 ms in separate Thread
                .log();
        infiniteFlux.subscribe((elem) -> System.out.println("Emitted value is: " + elem));
        Thread.sleep(3000);
    }

    @Test
    void canCollectLimitedNumberOfElementsFromInfiniteSequence() throws InterruptedException {
        Flux<Long> finiteFlux = Flux
                .interval(Duration.ofMillis(100))
                .take(3) // limit to 3 Elements
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void canCollectLimitedNumberOfElementsFromInfiniteSequenceAndConvert() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux
                .interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    void takesThreeSecondsWithDurationOfThreeSecs() {

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    void runsQuickerWithVirtualTime() {

        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();

    }

    /**
     * Mimic external service, convert String to List and wait a bit
     *
     * @param name
     * @return String s -> List[s, 'Z']
     */
    private List<String> callExternalConverter(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(name, "Z");
    }

    private class CustomException extends Throwable {
        private String message;

        public CustomException(Throwable e) {
            this.message = e.getMessage();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
