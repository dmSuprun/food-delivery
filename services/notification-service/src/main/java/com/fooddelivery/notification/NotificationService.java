package com.fooddelivery.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Builds push/SMS/email notifications about order status changes. */
public class NotificationService {

    public enum Channel { PUSH, SMS, EMAIL }

    public record Notification(Channel channel, String recipient, String text) {}

    private static final Map<String, String> TEMPLATES = Map.of(
            "PAID", "Order %s is paid. The restaurant is preparing your food.",
            "DELIVERING", "Order %s is on the way. The courier will arrive soon.",
            "DELIVERED", "Order %s is delivered. Bon appetit!",
            "CANCELLED", "Order %s was cancelled.");

    private final List<Notification> outbox = new ArrayList<>();

    public Notification notify(Channel channel, String recipient, String orderId, String status) {
        String template = TEMPLATES.get(status);
        if (template == null) {
            throw new IllegalArgumentException("No template for status " + status);
        }
        String text = String.format(template, orderId);
        if (channel == Channel.SMS && text.length() > 160) {
            text = text.substring(0, 157) + "...";
        }
        Notification n = new Notification(channel, recipient, text);
        outbox.add(n);
        return n;
    }

    public List<Notification> outbox() {
        return List.copyOf(outbox);
    }
}
