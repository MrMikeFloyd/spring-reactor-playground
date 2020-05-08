package de.maik.reactivespring.server.item.boundary.v2;

import de.maik.reactivespring.server.item.document.Item;
import de.maik.reactivespring.server.item.document.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemsHandler {

    private static final String ITEM_ID_PATH_VARIABLE = "itemId";
    private ItemRepository itemRepository;
    // Syntactic sugar for 404 response creation
    private static Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();

    @Autowired
    public ItemsHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);
    }

    Mono<ServerResponse> getOne(ServerRequest request) {
        String itemId = request.pathVariable(ITEM_ID_PATH_VARIABLE);
        Mono<Item> itemMono = itemRepository.findById(itemId);
        return itemMono
                .flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                .switchIfEmpty(notFoundMono);
    }

    /**
     * Changed behavior compared to v1: Don't return the actual created item,
     * return its location instead
     *
     * @param request request containing the item to be created
     * @return 200 if ok and a Location header containing the created item's resource location
     */
    Mono<ServerResponse> createOne(ServerRequest request) {
        Mono<Item> itemMono = request.bodyToMono(Item.class);
        return itemMono
                // Save first so we can use the item's generated id value during response creation
                .flatMap(item -> itemRepository.save(item))
                .flatMap(item -> ServerResponse.created(
                        URI.create(request.uri().toString() + "/" + item.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .build());
    }

    /**
     * Changed behaviour compared to v1: Return 204 for deleted item.
     *
     * @param request containing the item's id
     * @return 204 if ok
     */
    Mono<ServerResponse> deleteOne(ServerRequest request) {
        return ServerResponse.noContent().build(itemRepository.deleteById(request.pathVariable(ITEM_ID_PATH_VARIABLE)));
    }

    /**
     * Update an item:
     * <ul>
     *     <li>Load item from persistence layer</li>
     *     <li>Update its values</li>
     *     <li>Create response with new item in body</li>
     * </ul>
     *
     * @param request containing the new item
     * @return 200 if ok + the updated item, 404 otherwise
     */
    Mono<ServerResponse> updateOne(ServerRequest request) {
        String itemId = request.pathVariable(ITEM_ID_PATH_VARIABLE);
        Mono<Item> updatedItemMono = request
                .bodyToMono(Item.class)
                .flatMap(itemFromRequest ->
                        itemRepository
                                .findById(itemId)
                                .flatMap(itemFromRepo -> {
                                    itemFromRepo.setDescription(itemFromRequest.getDescription());
                                    itemFromRepo.setPrice(itemFromRequest.getPrice());
                                    return itemRepository.save(itemFromRepo);
                                })
                );
        return updatedItemMono
                .flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                .switchIfEmpty(notFoundMono);

    }
}
