package com.fooddelivery.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void addsDeliveryFeeForSmallOrders() {
        Order order = new Order(List.of(new Order.Line("Margherita", new BigDecimal("180"), 2)));
        assertEquals(new BigDecimal("360.00"), order.itemsTotal());
        assertEquals(new BigDecimal("410.00"), order.total());
    }

    @Test
    void freeDeliveryAboveThreshold() {
        Order order = new Order(List.of(
                new Order.Line("Pepperoni", new BigDecimal("220"), 2),
                new Order.Line("Cola", new BigDecimal("60"), 1)));
        assertEquals(new BigDecimal("500.00"), order.total());
    }

    @Test
    void rejectsEmptyOrderAndInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new Order(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new Order(List.of(new Order.Line("Soup", BigDecimal.TEN, 0))));
    }

    @Test
    void followsStatusLifecycle() {
        Order order = new Order(List.of(new Order.Line("Soup", BigDecimal.TEN, 1)));
        order.moveTo(OrderStatus.PAID);
        order.moveTo(OrderStatus.PREPARING);
        order.moveTo(OrderStatus.DELIVERING);
        order.moveTo(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.status());
    }

    @Test
    void forbidsInvalidTransition() {
        Order order = new Order(List.of(new Order.Line("Soup", BigDecimal.TEN, 1)));
        assertThrows(IllegalStateException.class, () -> order.moveTo(OrderStatus.DELIVERED));
    }
}
