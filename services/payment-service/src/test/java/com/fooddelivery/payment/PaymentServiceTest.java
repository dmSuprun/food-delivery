package com.fooddelivery.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private final PaymentService payments = new PaymentService();

    @Test
    void approvesValidTestCard() {
        PaymentService.Payment p = payments.pay("order-1", new BigDecimal("410.00"), "4242 4242 4242 4242");
        assertEquals(PaymentService.Result.APPROVED, p.result());
        assertEquals("**** **** **** 4242", p.maskedCard());
    }

    @Test
    void declinesCardWithWrongChecksum() {
        PaymentService.Payment p = payments.pay("order-2", BigDecimal.TEN, "4242 4242 4242 4241");
        assertEquals(PaymentService.Result.DECLINED, p.result());
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> payments.pay("order-3", BigDecimal.ZERO, "4242424242424242"));
    }
}
