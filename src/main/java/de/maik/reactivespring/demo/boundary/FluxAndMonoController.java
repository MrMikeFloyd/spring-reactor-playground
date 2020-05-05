package de.maik.reactivespring.demo.boundary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    /**
     * Returns Stream as Integer, emits on onComplete() event
     */
    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Returns as infinite stream (cold publisher, new instance for every client connecting),
     * emits element for every onNext() event
     */
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Returns Mono at onComplete()
     */
    @GetMapping("/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1).log();
    }

}
