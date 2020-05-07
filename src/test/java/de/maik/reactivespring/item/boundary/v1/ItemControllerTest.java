package de.maik.reactivespring.item.boundary.v1;

import de.maik.reactivespring.item.document.Item;
import de.maik.reactivespring.item.document.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@DirtiesContext
class ItemControllerTest {

    private static final String ITEMS_ENDPOINT_V1 = "/v1/items";
    private static final String PREDEFINED_ITEM_ID = "ITMID001";
    private static final String PREDEFINED_ITEM_DESCRIPTION = "Another item";
    private static final double PREDEFINED_ITEM_PRICE = 1.99;

    @Autowired
    WebTestClient webTestClient; // Use non-blocking client, TestRestTemplate would be blocking

    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    private void setUp() {
        setupTestDatabase();
    }

    @Test
    void gettingAllItemsWillReturnHttp200AndJsonOfAllItemsInTheRepository() {
        webTestClient.get().uri(ITEMS_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    assertThat(items)
                            .usingRecursiveFieldByFieldElementComparator()
                            .usingElementComparatorIgnoringFields("id")
                            .isEqualTo(createSampleItems());
                });
    }

    @Test
    void gettingSpecificItemByIdReturnsHttp200AndTheItem() {
        webTestClient.get().uri(ITEMS_ENDPOINT_V1.concat("/{itemId}"), PREDEFINED_ITEM_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(PREDEFINED_ITEM_ID)
                .jsonPath("$.description").isEqualTo(PREDEFINED_ITEM_DESCRIPTION)
                .jsonPath("$.price").isEqualTo(PREDEFINED_ITEM_PRICE);
    }

    @Test
    void gettingSpecificNonExistingItemByIdReturnsHttp404() {
        webTestClient.get().uri(ITEMS_ENDPOINT_V1.concat("/{itemId}"), "NOTAVAILABLE001")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void postingItemReturnsHttp201AndTheNewlyCreatedItem() {
        Item item = new Item(null, "Propain Tyee 2020 29", 3499.00);
        webTestClient.post().uri(ITEMS_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Item.class)
                .consumeWith(response -> assertThat(response.getResponseBody())
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(item));
    }

    @Test
    void deletingItemReturns200AndEmptyResponseBody() {
        webTestClient.delete().uri(ITEMS_ENDPOINT_V1.concat("/{itemId}"), PREDEFINED_ITEM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    private void setupTestDatabase() {
        itemRepository.deleteAll().thenMany(Flux.fromIterable(createSampleItems()))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Inserted Test Item: " + item))
                .blockLast(); // Block until onComplete to avoid premature test execution
    }

    private List<Item> createSampleItems() {
        return Arrays.asList(
                new Item(null, "Specialized Enduro Elite", 4999.00),
                new Item(null, "Rondo Ruut ST", 2399.99),
                new Item(null, "Santa Cruz Megatower", 7249.99),
                new Item(null, "Newmen Alloy Wheelset 650b x 30", 699.99),
                new Item(PREDEFINED_ITEM_ID, PREDEFINED_ITEM_DESCRIPTION, PREDEFINED_ITEM_PRICE));
    }

}