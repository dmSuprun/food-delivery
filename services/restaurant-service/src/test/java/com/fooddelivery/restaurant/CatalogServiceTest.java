package com.fooddelivery.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogServiceTest {

    private final CatalogService catalog = new CatalogService();

    @BeforeEach
    void setUp() {
        catalog.add(new Restaurant("1", "Pizza House", 4.7, List.of(
                new MenuItem("Margherita", "pizza", new BigDecimal("180")),
                new MenuItem("Pepperoni", "pizza", new BigDecimal("220")))));
        catalog.add(new Restaurant("2", "Sushi Bar", 4.2, List.of(
                new MenuItem("Philadelphia roll", "sushi", new BigDecimal("320")))));
        catalog.add(new Restaurant("3", "Burger Point", 3.9, List.of(
                new MenuItem("Cheeseburger", "burger", new BigDecimal("150")))));
    }

    @Test
    void searchesByRestaurantAndDishName() {
        assertEquals("Sushi Bar", catalog.search("sushi").get(0).name());
        assertEquals("Pizza House", catalog.search("pepperoni").get(0).name());
    }

    @Test
    void filtersByCategoryAndPrice() {
        List<Restaurant> result = catalog.filter("pizza", new BigDecimal("200"), 0);
        assertEquals(1, result.size());
        assertEquals("Pizza House", result.get(0).name());
    }

    @Test
    void filtersByRatingAndSortsDescending() {
        List<Restaurant> result = catalog.filter(null, null, 4.0);
        assertEquals(List.of("Pizza House", "Sushi Bar"), result.stream().map(Restaurant::name).toList());
    }
}
