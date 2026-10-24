package com.fooddelivery.delivery;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourierAssignerTest {

    private final CourierAssigner assigner = new CourierAssigner();
    private final CourierAssigner.Location restaurant = new CourierAssigner.Location(50.4501, 30.5234);

    @Test
    void picksNearestAvailableCourier() {
        List<CourierAssigner.Courier> couriers = List.of(
                new CourierAssigner.Courier("far", new CourierAssigner.Location(50.50, 30.60), true),
                new CourierAssigner.Courier("near-busy", new CourierAssigner.Location(50.4502, 30.5235), false),
                new CourierAssigner.Courier("near", new CourierAssigner.Location(50.4550, 30.5300), true));
        assertEquals("near", assigner.assign(restaurant, couriers, 10).orElseThrow().id());
    }

    @Test
    void returnsEmptyWhenNobodyInRange() {
        List<CourierAssigner.Courier> couriers = List.of(
                new CourierAssigner.Courier("lviv", new CourierAssigner.Location(49.8397, 24.0297), true));
        assertTrue(assigner.assign(restaurant, couriers, 10).isEmpty());
    }

    @Test
    void calculatesDistanceBetweenCities() {
        double kyivLviv = CourierAssigner.distanceKm(restaurant, new CourierAssigner.Location(49.8397, 24.0297));
        assertEquals(468, kyivLviv, 5);
    }
}
