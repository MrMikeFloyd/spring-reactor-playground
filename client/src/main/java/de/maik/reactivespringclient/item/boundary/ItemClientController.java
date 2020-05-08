package de.maik.reactivespringclient.item.boundary;

import de.maik.reactivespringclient.item.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
public class ItemClientController {

    @Value("${items.api.base.url}")
    String itemsApiBaseUrl;
    private static final String ITEMS_RESOURCE_V2 = "/v2/items";
    private WebClient webClient;

    @PostConstruct
    private void initialize() {
        // Create WebClient after class init to allow URL from value
        webClient = WebClient.create(itemsApiBaseUrl);
    }

    /**
     * Retrieve will provide access to response body directly
     * @return Items using retrieve
     */
    @GetMapping("/client/retrieve")
    public Flux<Item> retrieveAll() {
        return webClient.get().uri(ITEMS_RESOURCE_V2)
                .retrieve()
                .bodyToFlux(Item.class)
                .log("[Retrieve] GET items from Server: ");
    }

    /**
     * Exchange will provide access to entire server response
     * @return items using exchange
     */
    @GetMapping("/client/exchange")
    public Flux<Item> exchangeAll() {
        return webClient.get().uri(ITEMS_RESOURCE_V2)
                .exchange()
                .flatMapMany(serverResponse -> serverResponse.bodyToFlux(Item.class))
                .log("[Exchange] GET items from Server: ");
    }

}
