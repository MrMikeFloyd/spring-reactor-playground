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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@DirtiesContext
class ItemControllerTest {

    private static final String ITEM_ENDPOINT_V1 = "/v1/items";

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
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    assertThat(items)
                            .usingRecursiveFieldByFieldElementComparator()
                            .usingElementComparatorIgnoringFields("id")
                            .isEqualTo(createSampleItems());
                });
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
                new Item(null, "Newmen Alloy Wheelset 650b x 30", 699.99));
    }

}