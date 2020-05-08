package de.maik.reactivespringclient.item.boundary;

import de.maik.reactivespringclient.item.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
public class ItemClientController {

    @Value("${items.api.base.url}")
    String itemsApiBaseUrl;
    private static final String CLIENT_ITEMS_RESOURCE_ENDPOINT_URL = "/items";
    private static final String SERVER_ITEMS_RESOURCE_V2_ENDPOINT_URL = "/v2/items";
    private WebClient webClient;

    @PostConstruct
    private void initialize() {
        // Create WebClient after class init to allow URL from value
        webClient = WebClient.create(itemsApiBaseUrl);
    }

    /**
     * Returns all items. Demonstrates the use of 2 different methods to achieve this:
     * <ul>
     *     <li>useRetrieve = true uses retrieve => responseBody is returned directly</li>
     *     <li>useRetrieve = false uses exchange => serverResponse can be handled manually</li>
     * </ul>
     *
     * @return Items using retrieve
     */
    @GetMapping(CLIENT_ITEMS_RESOURCE_ENDPOINT_URL)
    public Flux<Item> getAll(@RequestParam(defaultValue = "true") boolean useRetrieve) {
        Flux<Item> itemsFlux;
        if (useRetrieve) {
            itemsFlux = webClient.get().uri(SERVER_ITEMS_RESOURCE_V2_ENDPOINT_URL)
                    .retrieve()
                    .bodyToFlux(Item.class)
                    .log("[Retrieve] GET all items from server: ");
        } else {
            itemsFlux = webClient.get().uri(SERVER_ITEMS_RESOURCE_V2_ENDPOINT_URL)
                    .exchange()
                    .flatMapMany(serverResponse -> serverResponse.bodyToFlux(Item.class))
                    .log("[Exchange] GET all items from server: ");
        }

        return itemsFlux;
    }

    @GetMapping(CLIENT_ITEMS_RESOURCE_ENDPOINT_URL + "/{itemId}")
    public Mono<Item> retrieveById(@PathVariable String itemId) {
        return webClient.get().uri(SERVER_ITEMS_RESOURCE_V2_ENDPOINT_URL.concat("/".concat(itemId)))
                .retrieve()
                .bodyToMono(Item.class)
                .log("GET item from server: ");
    }

    @PostMapping(CLIENT_ITEMS_RESOURCE_ENDPOINT_URL)
    public Mono<Item> addOne(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.post().uri(SERVER_ITEMS_RESOURCE_V2_ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("POST item to server: ");
    }

}
