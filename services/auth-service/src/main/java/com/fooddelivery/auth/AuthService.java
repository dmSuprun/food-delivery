package com.fooddelivery.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/** Registration and authentication of users with role separation. */
public class AuthService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");

    private final Map<String, User> users = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    public record User(String email, String salt, String passwordHash, Role role) {}

    public User register(String email, String password, Role role) {
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 chars and contain a letter and a digit");
        }
        if (users.containsKey(email)) {
            throw new IllegalStateException("User already exists");
        }
        String salt = newSalt();
        User user = new User(email, salt, hash(password, salt), role);
        users.put(email, user);
        return user;
    }

    public Optional<User> login(String email, String password) {
        User user = users.get(email);
        if (user == null || !user.passwordHash().equals(hash(password, user.salt()))) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public boolean hasAccess(User user, Role required) {
        return user.role() == Role.ADMIN || user.role() == required;
    }

    static boolean isStrongPassword(String password) {
        return password != null && password.length() >= 8
                && password.chars().anyMatch(Character::isLetter)
                && password.chars().anyMatch(Character::isDigit);
    }

    private String newSalt() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
