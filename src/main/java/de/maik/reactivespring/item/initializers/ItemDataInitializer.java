package de.maik.reactivespring.item.initializers;

import de.maik.reactivespring.item.document.Item;
import de.maik.reactivespring.item.document.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    private ItemRepository itemRepository;

    @Autowired
    public ItemDataInitializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Inserting Sample Item data.");
        setupSampleItemData();
    }

    private void setupSampleItemData() {
        itemRepository.deleteAll().thenMany(
                Flux.fromIterable(createSampleItems()))
                .flatMap(itemRepository::save)
                .thenMany(itemRepository.findAll())
                .subscribe(item -> log.info("Inserted sample item: " + item));
    }

    private List<Item> createSampleItems() {
        return Arrays.asList(
                new Item(null, "Specialized Enduro Elite", 4999.00),
                new Item(null, "Rondo Ruut ST", 2399.99),
                new Item(null, "Santa Cruz Megatower", 7249.99),
                new Item(null, "Newmen Alloy Wheelset 650b x 30", 699.99));
    }
}
