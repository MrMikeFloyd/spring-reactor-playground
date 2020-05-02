package de.maik.reactivespring.boundary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    /**
     * Returns Stream as Integer, browser waits until Flux is complete
     */
    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Returns as infinite stream (cold publisher, new instance for every client connecting)
     */
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

}
