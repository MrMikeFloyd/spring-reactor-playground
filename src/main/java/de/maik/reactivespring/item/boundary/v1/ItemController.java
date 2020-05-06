package de.maik.reactivespring.item.boundary.v1;

import de.maik.reactivespring.item.document.Item;
import de.maik.reactivespring.item.document.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class ItemController {

    private static final String ITEM_ENDPOINT_V1 = "/v1/items";
    private ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAll() {
        return itemRepository.findAll();
    }
}
