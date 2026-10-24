package com.fooddelivery.restaurant;

import java.util.List;

public record Restaurant(String id, String name, double rating, List<MenuItem> menu) {}
