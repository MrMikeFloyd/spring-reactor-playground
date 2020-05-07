package de.maik.reactivespring.item.boundary.v1;

import de.maik.reactivespring.item.document.Item;
import de.maik.reactivespring.item.document.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
