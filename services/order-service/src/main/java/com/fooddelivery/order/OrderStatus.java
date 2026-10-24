package com.fooddelivery.order;

import java.util.Set;

public enum OrderStatus {
    CREATED, PAID, PREPARING, DELIVERING, DELIVERED, CANCELLED;

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> Set.of(PAID, CANCELLED).contains(next);
            case PAID -> Set.of(PREPARING, CANCELLED).contains(next);
            case PREPARING -> next == DELIVERING;
            case DELIVERING -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
