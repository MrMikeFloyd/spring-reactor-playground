package de.maik.reactivespring.demo.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxHotAndColdHandsOnTest {

    @Test
    void coldPublisherWillEmitSameElementsToAnySubscriber() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G").delayElements(Duration.ofSeconds(1));
        stringFlux.subscribe(s -> System.out.println("Subscriber 1 received: " + s)); // receives A-G
        Thread.sleep(2000);
        stringFlux.subscribe(s -> System.out.println("Subscriber 2 received: " + s)); // also receives A-G
        Thread.sleep(4000);
    }

    @Test
    void hotPublisherWillEmitElementsOnlyOnce() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G").delayElements(Duration.ofSeconds(1));
        ConnectableFlux<String> connectableFlux = stringFlux.publish(); // Hot publisher
        connectableFlux.connect();

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 received: " + s)); // receives A-G
        Thread.sleep(3000);
        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 received: " + s)); // receives after 3 seconds => receives C & onward
        Thread.sleep(4000);
    }


}
