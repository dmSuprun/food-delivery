package com.fooddelivery.delivery;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Assigns the nearest available courier to a restaurant (haversine distance). */
public class CourierAssigner {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public record Location(double lat, double lon) {}

    public record Courier(String id, Location location, boolean available) {}

    public Optional<Courier> assign(Location restaurant, List<Courier> couriers, double maxDistanceKm) {
        return couriers.stream()
                .filter(Courier::available)
                .filter(c -> distanceKm(restaurant, c.location()) <= maxDistanceKm)
                .min(Comparator.comparingDouble(c -> distanceKm(restaurant, c.location())));
    }

    static double distanceKm(Location a, Location b) {
        double dLat = Math.toRadians(b.lat() - a.lat());
        double dLon = Math.toRadians(b.lon() - a.lon());
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(a.lat())) * Math.cos(Math.toRadians(b.lat()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }
}
