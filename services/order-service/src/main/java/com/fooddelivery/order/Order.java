package com.fooddelivery.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Customer order: total price calculation and status lifecycle. */
public class Order {

    static final BigDecimal DELIVERY_FEE = new BigDecimal("50.00");
    static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500.00");

    public record Line(String dish, BigDecimal price, int quantity) {}

    private final List<Line> lines;
    private OrderStatus status = OrderStatus.CREATED;

    public Order(List<Line> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (lines.stream().anyMatch(l -> l.quantity() <= 0)) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.lines = List.copyOf(lines);
    }

    public BigDecimal itemsTotal() {
        return lines.stream()
                .map(l -> l.price().multiply(BigDecimal.valueOf(l.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal total() {
        BigDecimal items = itemsTotal();
        BigDecimal fee = items.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? BigDecimal.ZERO : DELIVERY_FEE;
        return items.add(fee).setScale(2, RoundingMode.HALF_UP);
    }

    public OrderStatus status() {
        return status;
    }

    public void moveTo(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new IllegalStateException("Cannot change status from " + status + " to " + next);
        }
        status = next;
    }
}
