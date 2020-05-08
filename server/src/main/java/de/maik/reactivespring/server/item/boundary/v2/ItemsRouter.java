package de.maik.reactivespring.server.item.boundary.v2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Functional-style REST Router for the Items API
 */
@Configuration
public class ItemsRouter {

    private static final String ITEMS_ENDPOINT_V2 = "/v2/items";
    private static final String ITEM_ID_PATH_VARIABLE = "/{itemId}";

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ITEMS_ENDPOINT_V2).and(accept(MediaType.APPLICATION_JSON))
                        , itemsHandler::getAll)
                .andRoute(GET(ITEMS_ENDPOINT_V2 + ITEM_ID_PATH_VARIABLE).and(accept(MediaType.APPLICATION_JSON))
                        , itemsHandler::getOne)
                .andRoute(POST(ITEMS_ENDPOINT_V2).and(accept(MediaType.APPLICATION_JSON))
                        , itemsHandler::createOne)
                .andRoute(DELETE(ITEMS_ENDPOINT_V2 + ITEM_ID_PATH_VARIABLE).and(accept(MediaType.APPLICATION_JSON))
                        , itemsHandler::deleteOne)
                .andRoute(PUT(ITEMS_ENDPOINT_V2 + ITEM_ID_PATH_VARIABLE).and(accept(MediaType.APPLICATION_JSON))
                        , itemsHandler::updateOne);
    }
}
