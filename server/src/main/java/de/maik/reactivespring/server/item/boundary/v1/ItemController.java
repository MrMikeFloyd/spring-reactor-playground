package de.maik.reactivespring.server.item.boundary.v1;

import de.maik.reactivespring.server.item.document.Item;
import de.maik.reactivespring.server.item.document.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Classic, Spring-Style REST Controller for Items API
 */
@RestController
@Slf4j
public class ItemController {

    private static final String ITEMS_ENDPOINT_V1 = "/v1/items";
    private ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping(ITEMS_ENDPOINT_V1)
    public Flux<Item> getAll() {
        return itemRepository.findAll();
    }

    @GetMapping(ITEMS_ENDPOINT_V1 + "/{itemId}")
    public Mono<ResponseEntity<Item>> getOne(@PathVariable String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEMS_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createOne(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @DeleteMapping(ITEMS_ENDPOINT_V1 + "/{itemId}")
    public Mono<Void> deleteOne(@PathVariable String itemId) {
        return itemRepository.deleteById(itemId);
    }

    @PutMapping(ITEMS_ENDPOINT_V1 + "/{itemId}")
    public Mono<ResponseEntity<Item>> updateOne(@PathVariable String itemId,
                                                @RequestBody Item itemFromRequest) {
        return itemRepository.findById(itemId)
                .flatMap(itemFromRepository -> {
                    itemFromRepository.setDescription(itemFromRequest.getDescription());
                    itemFromRepository.setPrice(itemFromRequest.getPrice());
                    return itemRepository.save(itemFromRepository);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
