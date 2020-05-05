package de.maik.reactivespring.demo.boundary.router;

import de.maik.reactivespring.demo.boundary.handler.SampleHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Maps an incoming request to an appropriate handler
 */
@Configuration
public class RouterConfig {

    private static final String FLUX_URI = "/functional/flux";
    private static final String MONO_URI = "/functional/mono";

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandler handler) {
        return RouterFunctions
                .route(GET(FLUX_URI).and(accept(MediaType.APPLICATION_JSON)), handler::flux)
                .andRoute(GET(MONO_URI).and(accept(MediaType.APPLICATION_JSON)), handler::mono);
    }

}
