package com.fooddelivery.restaurant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Restaurant catalog with search and filters (category, price, rating). */
public class CatalogService {

    private final List<Restaurant> restaurants = new ArrayList<>();

    public void add(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public List<Restaurant> search(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        return restaurants.stream()
                .filter(r -> r.name().toLowerCase(Locale.ROOT).contains(q)
                        || r.menu().stream().anyMatch(i -> i.name().toLowerCase(Locale.ROOT).contains(q)))
                .toList();
    }

    public List<Restaurant> filter(String category, BigDecimal maxPrice, double minRating) {
        return restaurants.stream()
                .filter(r -> r.rating() >= minRating)
                .filter(r -> r.menu().stream().anyMatch(i ->
                        (category == null || i.category().equalsIgnoreCase(category))
                                && (maxPrice == null || i.price().compareTo(maxPrice) <= 0)))
                .sorted(Comparator.comparingDouble(Restaurant::rating).reversed())
                .toList();
    }
}
