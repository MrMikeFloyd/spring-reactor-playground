package de.maik.reactivespring.item.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {

    private static final String PREDEFINDED_ITEM_ID = "itm001";
    private static final String PREDEFINED_ITEM_ID_DESCRIPTION_TEXT = "Some other item";
    private static final String RONDO_RUUT_ST_DESCRIPTION = "Rondo Ruut ST";

    @Autowired
    ItemRepository itemRepository;

    private List<Item> items = Arrays.asList(
            new Item(null, "Specialized Enduro Elite", 4999.00),
            new Item(null, RONDO_RUUT_ST_DESCRIPTION, 2399.99),
            new Item(null, "Santa Cruz Megatower", 7249.99),
            new Item(null, "Newmen Alloy Wheelset 650b x 30", 699.99),
            new Item(PREDEFINDED_ITEM_ID, PREDEFINED_ITEM_ID_DESCRIPTION_TEXT, 1.99), // Will use predefined id and generate one otherwise
            new Item(null, PREDEFINED_ITEM_ID_DESCRIPTION_TEXT, 2.99));

    @BeforeEach
    void setUp() {
        fillItemsRepository();
    }

    @Test
    void retrievesExpectedNumberOfItemsFromRepository() {
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(items.size())
                .verifyComplete();
    }

    @Test
    void readsSpecificSingleItemById() {
        StepVerifier.create(itemRepository.findById(PREDEFINDED_ITEM_ID))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals(PREDEFINED_ITEM_ID_DESCRIPTION_TEXT))
                .verifyComplete();
    }

    @Test
    void returnsTwoMatchingItemsWhenSearchingByDescription() {
        StepVerifier.create(itemRepository
                .findByDescription(PREDEFINED_ITEM_ID_DESCRIPTION_TEXT)
                .log())
                .expectSubscription()
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void returnsSavedItem() {
        Mono<Item> savedItem = itemRepository.save(new Item(null, "A new item", 42.01));
        StepVerifier.create(savedItem.log())
                .expectSubscription()
                .expectNextMatches(item ->
                        item.getId() != null &&
                                item.getDescription().equals("A new item") &&
                                item.getPrice() == 42.01)
                .verifyComplete();
    }

    @Test
    void updatesItemPriceByMatchingDescription() {
        Flux<Item> updatedItem = itemRepository.findByDescription(RONDO_RUUT_ST_DESCRIPTION)
                .map(item -> {
                    item.setPrice(42.00); // Update the price
                    return item;
                })
                .flatMap(item -> itemRepository.save(item));// save the updated item instance

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 42.00)
                .verifyComplete();
    }

    @Test
    void findsAndDeletedItemById() {
        Mono<Void> deletedItem = itemRepository.findById(PREDEFINDED_ITEM_ID)
                .map(Item::getId)
                .flatMap(itemId -> itemRepository.deleteById(itemId));

        // Execute operation
        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        // Verify that the item is in fact gone
        StepVerifier.create(itemRepository.findById(PREDEFINDED_ITEM_ID))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    private void fillItemsRepository() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Added item: " + item))
                .blockLast(); // Block until all items have been inserted to avoid premature test case execution
    }

}