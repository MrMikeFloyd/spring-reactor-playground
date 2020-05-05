package de.maik.reactivespring.inventory.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {

    private static final String PREDEFINDED_ITEM_ID = "itm001";
    private static final String PREDEFINED_ITEM_ID_DESCRIPTION_TEXT = "An item with a predefined id value";

    @Autowired
    ItemRepository itemRepository;

    private List<Item> items = Arrays.asList(
            new Item(null, "Specialized Enduro Elite", 4999.00),
            new Item(null, "Rondo Ruut ST", 2399.99),
            new Item(null, "Santa Cruz Megatower", 7249.99),
            new Item(null, "Newmen Alloy Wheelset 650b x 30", 699.99),
            new Item(PREDEFINDED_ITEM_ID, PREDEFINED_ITEM_ID_DESCRIPTION_TEXT, 1.99));

    @BeforeEach
    void setUp() {
        fillItemsRepository();
    }

    @Test
    void canRetrieveExpectedNumberOfItemsFromRepository() {
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(items.size())
                .verifyComplete();
    }

    @Test
    void canReadSpecificItemById() {
        StepVerifier.create(itemRepository.findById(PREDEFINDED_ITEM_ID))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals(PREDEFINED_ITEM_ID_DESCRIPTION_TEXT))
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