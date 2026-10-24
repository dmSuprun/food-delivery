package com.fooddelivery.payment;

import java.math.BigDecimal;
import java.util.UUID;

/** Validates payment requests before they are sent to the payment gateway. */
public class PaymentService {

    public enum Result { APPROVED, DECLINED }

    public record Payment(String id, String orderId, BigDecimal amount, Result result, String maskedCard) {}

    public Payment pay(String orderId, BigDecimal amount, String cardNumber) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\s", "");
        Result result = isValidCard(digits) ? Result.APPROVED : Result.DECLINED;
        return new Payment(UUID.randomUUID().toString(), orderId, amount, result, mask(digits));
    }

    /** Luhn checksum used by all major card networks. */
    static boolean isValidCard(String digits) {
        if (!digits.matches("\\d{13,19}")) {
            return false;
        }
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }

    static String mask(String digits) {
        if (digits.length() < 4) {
            return "****";
        }
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }
}
