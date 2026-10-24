package com.fooddelivery.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private final AuthService auth = new AuthService();

    @Test
    void registersAndLogsInUser() {
        auth.register("client@example.com", "secret123", Role.CUSTOMER);
        assertTrue(auth.login("client@example.com", "secret123").isPresent());
    }

    @Test
    void rejectsWrongPassword() {
        auth.register("client@example.com", "secret123", Role.CUSTOMER);
        assertTrue(auth.login("client@example.com", "wrong999").isEmpty());
    }

    @Test
    void rejectsWeakPasswordAndInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> auth.register("a@b.com", "short", Role.CUSTOMER));
        assertThrows(IllegalArgumentException.class, () -> auth.register("not-an-email", "secret123", Role.CUSTOMER));
    }

    @Test
    void rejectsDuplicateRegistration() {
        auth.register("rest@example.com", "secret123", Role.RESTAURANT);
        assertThrows(IllegalStateException.class, () -> auth.register("rest@example.com", "secret123", Role.RESTAURANT));
    }

    @Test
    void adminHasAccessToEveryRole() {
        AuthService.User admin = auth.register("admin@example.com", "admin1234", Role.ADMIN);
        AuthService.User courier = auth.register("courier@example.com", "courier123", Role.COURIER);
        assertTrue(auth.hasAccess(admin, Role.RESTAURANT));
        assertTrue(auth.hasAccess(courier, Role.COURIER));
        assertFalse(auth.hasAccess(courier, Role.RESTAURANT));
    }
}
