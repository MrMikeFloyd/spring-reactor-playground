package de.maik.reactivespringclient.item.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory item representing stock
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String id;
    private String description;
    private double price;
}

