package de.maik.reactivespring.server.demo.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxWithBackpressureHandsOnTest {

    @Test
    void canReadFromFluxWithBackpressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .thenRequest(1)
                .thenRequest(1)
                .expectNext(3) // Subscriber controls amount of elements received with onNext call
                .thenRequest(1)
                .thenCancel()
                .verify();
    }

    /**
     * Programmatic setup of subscribing to a Flux. subscribe method defines subscription,
     * then
     * <ul>
     *  <li>Consumer for onNext</li>
     *  <li>Consumer for onError</li>
     *  <li>Consumer for onComplete</li>
     *  <li>Config for request backpressure</li>
     * <ul/>
     */
    @Test
    void backPressureDemo() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(
                System.out::println,
                e -> System.out.println("An error occured: " + e.getMessage()),
                () -> System.out.println("Subscription complete."), // no complete event - only 3 elements are requested
                subscription -> subscription.request(3)); // read 3 at a time
    }

    /**
     * Implement BaseSubscriber for customized subscription
     * (i.e. custom events that end a subscription)
     */
    @Test
    void backPressureDemoWithCustomizedBackpressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Element received: " + value);
                if (value == 4) { // Cancel subscription when condition is met
                    cancel();
                }
            }
        });
    }
}
