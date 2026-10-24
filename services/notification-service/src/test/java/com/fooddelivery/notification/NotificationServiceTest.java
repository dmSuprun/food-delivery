package com.fooddelivery.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private final NotificationService service = new NotificationService();

    @Test
    void buildsMessageFromTemplate() {
        NotificationService.Notification n =
                service.notify(NotificationService.Channel.PUSH, "client-1", "A-100", "DELIVERING");
        assertEquals("Order A-100 is on the way. The courier will arrive soon.", n.text());
        assertEquals(1, service.outbox().size());
    }

    @Test
    void truncatesLongSms() {
        String longId = "X".repeat(200);
        NotificationService.Notification n =
                service.notify(NotificationService.Channel.SMS, "+380000000000", longId, "PAID");
        assertEquals(160, n.text().length());
    }

    @Test
    void rejectsUnknownStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.notify(NotificationService.Channel.EMAIL, "client-1", "A-1", "UNKNOWN"));
    }
}
